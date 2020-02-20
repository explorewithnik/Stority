package com.app.homeSpace.homeSpace.data

import androidx.room.Embedded
import androidx.room.Relation

class Category_SubCategory_Relation {

    @Embedded
    var homeSpaceTable: HomeSpaceTable? = HomeSpaceTable()

    @Relation(parentColumn = "id", entityColumn = "id")
    var subCategoryListTable: MutableList<SubCategoryTable>? = ArrayList()
}