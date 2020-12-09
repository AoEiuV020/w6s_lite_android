package com.foreveross.atwork.modules.organization.manager

import android.content.Context
import androidx.annotation.Nullable
import com.foreverht.db.service.repository.DeptRepository
import com.foreverht.db.service.repository.EmployeeRepository
import com.foreveross.atwork.api.sdk.net.HttpResultException
import com.foreveross.atwork.api.sdk.organization.OrganizationAsyncNetService.OnEmployeeTreeLoadListener
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult
import com.foreveross.atwork.infrastructure.model.ContactModel
import com.foreveross.atwork.infrastructure.model.orgization.Department
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentTree
import com.foreveross.atwork.infrastructure.utils.extension.coroutineScope
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction
import com.foreveross.atwork.modules.contact.util.EmpSeniorHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

object DeptManager : IDeptManager {

    override fun updateDeptsDbSync(parentId: String, filterSenior: Boolean, rankView: Boolean, results: List<OrganizationResult>) {
        val query = DepartmentTree.getQuery(filterSenior, rankView)

        val deptsHandling = ArrayList(results)
                .also { orgResults -> orgResults.addAll(results.flatMap { it.children }) }
                .map { it.toDepartment() }

        val empsHandling = results
                .flatMap { it.employeeResults }
                .map { it.toEmployee() }

        val treesHandling = deptsHandling
                .mapIndexed { index, department -> DepartmentTree(id = department.mId, parentId = parentId, type = "CORP", query = query, sort = index)}
                .toMutableList()
                .apply { addAll(empsHandling.mapIndexed { index, employee -> DepartmentTree(id = employee.id, parentId = parentId, type = "EMPLOYEE", query = query, sort = index) }) }

        DeptRepository.batchInsertDepts(deptsHandling)
        EmployeeRepository.getInstance().batchInsertEmployee(empsHandling)
        DeptRepository.batchInsertDeptTrees(parentId, query, treesHandling)

    }

    fun queryUserOrgAndEmployeeCompat(context: Context, orgCode: String, orgId: String, level: Int, range: QureyOrganizationViewRange, expandEmpTreeAction: ExpandEmpTreeAction, onEmployeeTreeLoadListener: OnEmployeeTreeLoadListener) {
        queryUserOrgAndEmployee(context, orgCode, orgId, level, range, expandEmpTreeAction)
                .flowOn(Dispatchers.IO)
                .onEach { onEmployeeTreeLoadListener.onSuccess(it.first, it.second) }
                .launchIn(context.coroutineScope)
    }

    fun queryUserOrgAndEmployee(context: Context, orgCode: String, orgId: String, level: Int, range: QureyOrganizationViewRange, expandEmpTreeAction: ExpandEmpTreeAction): Flow<Pair<Int, List<OrganizationResult>?>> = flow {

        //只处理第一页的本地数据, 避免服务器里节点数据变更, 导致复杂的计算同步操作
        if(range.havingLocalFeature()) {
            val result = queryUserOrgAndEmployeeLocalSync(context, orgCode, orgId, level, range, expandEmpTreeAction)
            emit(ContactModel.LOAD_STATUS_LOADED_LOCAL to result)
        }


        queryUserOrgAndEmployeeFromRemote(context, orgCode, orgId, level, range, expandEmpTreeAction).flowOn(Dispatchers.IO).catch {  }.collect { emit(it) }

    }


    @Nullable
    private fun queryUserOrgAndEmployeeLocalSync(context: Context, orgCode: String, orgId: String, level: Int, range: QureyOrganizationViewRange, expandEmpTreeAction: ExpandEmpTreeAction): List<OrganizationResult>? {
        val filterSenior = EmpSeniorHelper.getFilterSeniorAction(context, expandEmpTreeAction, orgCode)?: return null
        //选人模式时, 需要启动聊天视图过滤
        val rankView = !expandEmpTreeAction.mIsSelectMode

        val pairData =  DeptRepository.queryDepartmentTrees(orgId, range.orgSkip, range.orgLimit, range.employeeSkip, range.employeeLimit, DepartmentTree.getQuery(filterSenior, rankView))
        val departmentList = pairData.first
        val employeesList = pairData.second

        val allOrganizationResultList = departmentList.map { transfer(it) }
        allOrganizationResultList.forEach {
            it.children = ArrayList()
            it.employeeResults = ArrayList()
        }

        val topOrganizationResult = allOrganizationResultList.find { it.id == orgId }
        topOrganizationResult?.loadingStatus = ContactModel.LOAD_STATUS_LOADING
        topOrganizationResult?.children = allOrganizationResultList.toMutableList().apply { removeAll { it.id == orgId } }
        topOrganizationResult?.employeeResults = employeesList.map { EmployeeResult.buildEmployee(it) }


        return topOrganizationResult
                ?.let { arrayListOf(topOrganizationResult) }
                ?.also { OrganizationManager.getInstance().assembleOrganizationList(it, level, range, ContactModel.LOAD_STATUS_LOADED_LOCAL) }
    }


    private fun transfer(department: Department): OrganizationResult {
        return OrganizationResult().apply {
            id = department.mId
            domainId = department.mDomainId
            parentOrgId = department.mParentOrgId
            orgCode = department.mOrgCode
            name = department.mName
            employeeCount = department.mEmployeeCount
            allEmployeeCount = department.mAllEmployeeCount
            path = department.mPath
        }

    }

    private fun queryUserOrgAndEmployeeFromRemote(context: Context, orgCode: String, orgId: String, level: Int, range: QureyOrganizationViewRange, expandEmpTreeAction: ExpandEmpTreeAction) = callbackFlow<Pair<Int, List<OrganizationResult>>> {

        OrganizationManager.getInstance().queryUserOrgAndEmployeeFromRemote(context, orgCode, orgId, level, range, expandEmpTreeAction, object : OnEmployeeTreeLoadListener {
            override fun onSuccess(loadedStatus: Int, organizationList: List<OrganizationResult>) {
                offer(loadedStatus to organizationList)
                close()
            }

            override fun networkFail(errorCode: Int, errorMsg: String?) {
                close(HttpResultException(errorCode, errorMsg))

            }

        })

        awaitClose()
    }

}