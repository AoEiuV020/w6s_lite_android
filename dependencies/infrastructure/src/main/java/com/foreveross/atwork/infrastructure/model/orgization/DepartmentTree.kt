package com.foreveross.atwork.infrastructure.model.orgization

data class DepartmentTree(
        val id: String,

        val parentId: String,

        val type: String,

        val sort: Int = -1,

        val query: Int
) {
    companion object {

        fun getQuery(filterSenior: Boolean, rankView: Boolean): Int {
            var filterSeniorVal = 0b00
            if(filterSenior) {
                filterSeniorVal = 0b01
            }


            var rankViewVal = 0b00
            if(rankView) {
                rankViewVal = 0b10
            }

            return filterSeniorVal and rankViewVal

        }
    }
}