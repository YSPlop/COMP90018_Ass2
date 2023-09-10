package com.example.safecircle

abstract class User(
    open var username: String? = null,
    open var password: String? = null
)

data class Parent(
    override var username: String? = null,
    override var password: String? = null
) : User()

data class Child(
    override var username: String? = null,
    override var password: String? = null,
    var locationId: String? = null
) : User()

data class Family(
    var id: String? = null,
    //val parents: MutableMap<String, Parent> = mutableMapOf(),
    //val kids: MutableMap<String, Child> = mutableMapOf()
)

