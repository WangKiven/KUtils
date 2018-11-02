package com.kiven.sample.xutils.db.entity

import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table

@Table(name = "User")
class User(
        @Column(name = "rId", isId = true, autoGen = true)
        var rId: Int? = null,
        @Column(name = "name")
        var name: String? = null,
        @Column(name = "birthday")
        var birthday: String? = null,
        @Column(name = "gId")
        var gId: String? = null
){
        constructor():this(null, null, null, null)

        override fun toString(): String {
                return "User(rId=$rId, name=$name, birthday=$birthday, gId=$gId)"
        }

}

data class Company(val companyName: String, val starDay: String, val code: String)