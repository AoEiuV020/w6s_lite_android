package com.foreverht.workplus.module.contact.fragment

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverht.workplus.module.contact.activity.KEY_DEPARTMENT
import com.foreverht.workplus.module.contact.activity.ShowDepartmentActivity
import com.foreverht.workplus.module.contact.adapter.DepartmentEmployeeAdapter
import com.foreverht.workplus.module.contact.adapter.DepartmentNodeAdapter
import com.foreverht.workplus.module.contact.component.BaseContactItemLoadMoreView
import com.foreverht.workplus.module.contact.component.DepartmentPathAdapter
import com.foreverht.workplus.module.contact.utlis.makeDepartmentPathList
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.organization.OrganizationAsyncNetService
import com.foreveross.atwork.api.sdk.organization.requstModel.QureyOrganizationViewRange
import com.foreveross.atwork.api.sdk.organization.responseModel.EmployeeResult
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.orgization.Department
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentPath
import com.foreveross.atwork.infrastructure.model.user.User
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.manager.UserManager
import com.foreveross.atwork.manager.model.ExpandEmpTreeAction
import com.foreveross.atwork.modules.contact.activity.PersonalInfoActivity
import com.foreveross.atwork.modules.group.component.HorizontalListView
import com.foreveross.atwork.modules.search.activity.NewSearchActivity
import com.foreveross.atwork.modules.search.model.NewSearchControlAction
import com.foreveross.atwork.modules.search.model.SearchContent
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.ErrorHandleUtil


class ShowDepartmentFragment : BackHandledFragment() {

    private lateinit var scrollView:  NestedScrollView
    private lateinit var backBtn:     ImageView
    private lateinit var title:       TextView
    private lateinit var rightText:   TextView
    private lateinit var searchEt:    TextView

    private lateinit var departPathListView:    HorizontalListView
    private lateinit var departNodeListView:    RecyclerView
    private lateinit var employeeListView:      RecyclerView

    private lateinit var departmentPathAdapter: DepartmentPathAdapter
    private var departmentNodeAdapter: DepartmentNodeAdapter? = null
    private var employeeAdapter:       DepartmentEmployeeAdapter? = null

    private val organizations: MutableList<OrganizationResult> = mutableListOf()
    private val employeeResults: MutableList<EmployeeResult> = mutableListOf()

    private lateinit var department: Department

    private var progressDialogHelper: ProgressDialogHelper? = null

    private var employeeSkip        = 0
    private var organizationSkip    = 0

    /**
     * 当前请求方式 0: 组织和雇员  1 雇员 2 组织
     * 因为请求接口只有一个入口，所以用此参数来区分失败情况下adapter的通知变更
     */
    private var currentQueryType = 0

    private var departmentPathList:Array<DepartmentPath> = arrayOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_department, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        registerListener()
    }

    override fun findViews(view: View) {
        scrollView = view.findViewById(R.id.nsv_view)
        backBtn = view.findViewById(R.id.title_bar_common_back)
        title = view.findViewById(R.id.title_bar_common_title)
        title.text = getString(R.string.organization)
        rightText = view.findViewById(R.id.title_bar_common_right_text)
        rightText.visibility = View.GONE
        searchEt = view.findViewById(R.id.et_search_department)
        departPathListView = view.findViewById(R.id.lv_department_path)
        departNodeListView = view.findViewById(R.id.rv_department_next_node_list)
        val nextNodelayoutManager = LinearLayoutManager(context)
        nextNodelayoutManager.orientation = LinearLayoutManager.VERTICAL
        departNodeListView.layoutManager = nextNodelayoutManager
        employeeListView = view.findViewById(R.id.rv_department_employee_list)
        val employeeLayoutManager = LinearLayoutManager(context)
        employeeLayoutManager.orientation = LinearLayoutManager.VERTICAL
        employeeListView.layoutManager = employeeLayoutManager

        val diametric = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(diametric)
        val a = diametric.heightPixels * 60 / 85
        employeeListView.getLayoutParams().height = a
    }

    private fun initData() {
        if (arguments == null) {
            return
        }
        department = arguments!!.getParcelable(KEY_DEPARTMENT)!!
        setPathView()
        loadRemoteData()
    }

    private fun loadRemoteData() {
        progressDialogHelper = ProgressDialogHelper(context)
        progressDialogHelper?.show()
        makeRequest(QureyOrganizationViewRange())
    }

    private fun makeRequest(range: QureyOrganizationViewRange) {
        val expandEmpTreeAction = ExpandEmpTreeAction.newExpandEmpTreeAction().setSelectMode(false)

        OrganizationManager.getInstance().queryUserOrgAndEmployeeFromRemote(context, department.mOrgCode, department.mId, 0, range, expandEmpTreeAction, object : OrganizationAsyncNetService.OnEmployeeTreeLoadListener {
            override fun networkFail(errorCode: Int, errorMsg: String) {

                progressDialogHelper?.dismiss()
                if (isDetached) {
                    return
                }
                if (!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                    AtworkToast.showResToast(R.string.network_not_good)
                    if (currentQueryType == 1)  {
                        employeeAdapter?.loadMoreFail()
                    }
                    if (currentQueryType == 2) {
                        departmentNodeAdapter?.loadMoreFail()
                    }
                }
            }

            override fun onSuccess(loadedStatus: Int, organizationList: List<OrganizationResult>) {
                progressDialogHelper?.dismiss()
                if (isDetached) {
                    return
                }
                val orgList = organizationList[0].children
                organizations.addAll(orgList)
                setNodeView(orgList.size > QureyOrganizationViewRange.QueryRangeConst.queryLimit - 1)
                val employeeList = organizationList[0].employeeResults
                employeeResults.addAll(employeeList)
                setEmployeeView(employeeList.size > QureyOrganizationViewRange.QueryRangeConst.queryLimit - 1)
            }
        })
    }

    private fun setPathView() {
        departmentPathList = makeDepartmentPathList(department.mFullNamePath, department.mPath)
        departmentPathAdapter = DepartmentPathAdapter(context!!, departmentPathList)
        departPathListView.adapter = departmentPathAdapter
    }

    private fun setNodeView(needLoadMore: Boolean) {
        departNodeListView.visibility = if (ListUtil.isEmpty(organizations)) View.GONE else  View.VISIBLE
        if (departmentNodeAdapter == null) {
            departmentNodeAdapter = DepartmentNodeAdapter(organizations)
            departNodeListView.adapter = departmentNodeAdapter
            departmentNodeAdapter?.setOnItemClickListener { _, _, position ->
                try {
                    val organization = organizations[position]
                    searchDepartments(organization.id, organization.name)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            departmentNodeAdapter?.notifyDataSetChanged()
        }
//        if (departmentNodeAdapter!!.itemCount > 100) {
//            departmentNodeAdapter?.loadMoreComplete()
//        }
//        if (needLoadMore) {
//            currentQueryType  = 2
//            val queryRange = QureyOrganizationViewRange()
//            organizationSkip += QureyOrganizationViewRange.QueryRangeConst.queryLimit
//            queryRange.orgSkip = organizationSkip
//            queryRange.employeeLimit = 0
//            queryRange.employeeSkip = 0
//            departmentNodeAdapter?.apply {
//                setOnLoadMoreListener({
//                    if (departmentNodeAdapter!!.itemCount < 100) {
//                        makeRequest(queryRange)
//                    } else {
//                        departmentNodeAdapter?.loadMoreComplete()
//                    }
//                }, departNodeListView)
//                setLoadMoreView(BaseContactItemLoadMoreView())
//            }
//            if (departmentNodeAdapter!!.itemCount > 100) {
//                departmentNodeAdapter?.loadMoreComplete()
//            }
//
//        } else {
//            departmentNodeAdapter?.loadMoreComplete()
//        }

    }

    private fun setEmployeeView(needLoadMore: Boolean) {
        employeeListView.visibility = if (ListUtil.isEmpty(employeeResults)) View.GONE else  View.VISIBLE
        if (employeeAdapter == null) {
            employeeAdapter = DepartmentEmployeeAdapter(employeeResults)
            employeeListView.adapter = employeeAdapter
            employeeAdapter?.apply {
                setOnItemClickListener{_, _, position ->
                    try {

                        val employee = employeeResults[position]
                        UserManager.getInstance().queryUserByUserId(activity, employee.userId, employee.domainId, object : UserAsyncNetService.OnQueryUserListener{
                            override fun onSuccess(user: User) {
                                val intent = PersonalInfoActivity.getIntent(context, user)
                                startActivity(intent)
                            }

                            override fun networkFail(errorCode: Int, errorMsg: String?) {

                            }

                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        } else {
            employeeAdapter?.notifyDataSetChanged()
        }
        if (needLoadMore) {
            currentQueryType  = 1
            val queryRange = QureyOrganizationViewRange()
            employeeSkip += QureyOrganizationViewRange.QueryRangeConst.queryLimit
            queryRange.employeeSkip = employeeSkip
            queryRange.orgLimit = 0
            queryRange.orgSkip = 0
            employeeAdapter?.apply {
                setOnLoadMoreListener({
                    makeRequest(queryRange)
                }, employeeListView)
                setLoadMoreView(BaseContactItemLoadMoreView())
            }
        } else {
            employeeAdapter?.loadMoreComplete()
        }
    }

    private fun registerListener() {
        backBtn.setOnClickListener {
            onBackPressed()
        }

        searchEt.setOnClickListener {
            val searchControlAction = NewSearchControlAction()
            searchControlAction.searchContentList = arrayOf(SearchContent.SEARCH_USER, SearchContent.SEARCH_DEPARTMENT)
            val intent = NewSearchActivity.getIntent(context, searchControlAction)
            startActivity(intent)
        }

        departPathListView.setOnItemClickListener { _, _, position, _ ->
            try {
                if (position != departmentPathList.size -1) {
                    progressDialogHelper?.show()
                    val path = departmentPathList[position]
                    searchDepartments(path.mDepartmentPathId, path.mDepartmentPathName)
                }


            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun searchDepartments(searchId: String, searchValue: String) {
        progressDialogHelper?.show()
        val listRange = mutableListOf<String>()
        listRange.add(department.mOrgCode)
        OrganizationManager.getInstance().searchDepartmentRemote(context, "", searchValue, null, object : OrganizationManager.RemoteSearchDepartmentsListener {
            override fun onSuccess(searchKeyCallBack: String, departments: List<Department>) {
                progressDialogHelper?.dismiss()
                if (ListUtil.isEmpty(departments)) {
                    return
                }

                for (department in departments) {
                    if (searchId == department.mId) {
                        ShowDepartmentActivity.startActivity(activity as Activity, department)
                        break
                    }
                }
            }

            override fun networkFail(errorCode: Int, errorMsg: String) {
                progressDialogHelper?.dismiss()
                AtworkToast.showToast(resources.getString(R.string.contact_search_fail))
            }
        })
    }

    override fun onBackPressed(): Boolean {
        activity?.finish()
        return false
    }

}