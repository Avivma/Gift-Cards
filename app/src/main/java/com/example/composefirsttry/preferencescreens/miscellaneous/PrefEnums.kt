package com.example.composefirsttry.preferencescreens.miscellaneous

enum class ParentCategoryEnum (val text: String){
    SWEETS ("sweets"),
    SALTS ("salts")
}

enum class CategoryEnum (val text: String, val index: Int){
    //    sweets:
    CANDY ("candy", 10),
    CHOCOLATE ("chocolate", 22),

    //    salts:
    OMELETTE ("omelette", 41),
    TOAST ("toast", 53);

    companion object {
      //more info on: .toCollection(ArrayList() - https://stackoverflow.com/questions/40036160/how-to-convert-intarray-to-arraylistint-in-kotlin
        fun getSweets(): ArrayList<CategoryEnum> = arrayOf(CANDY, CHOCOLATE).toCollection(ArrayList())
        fun getSalts(): ArrayList<CategoryEnum> = arrayOf(OMELETTE, TOAST).toCollection(ArrayList())
        fun getAll(): ArrayList<CategoryEnum> = values().toCollection(ArrayList())
//        fun getEnum(text: String): CategoryEnum = getAll().find { categoryEnum -> categoryEnum.text == text }!!
    }
}

const val SHARD_PREF_NAME = "shard_pref_name"