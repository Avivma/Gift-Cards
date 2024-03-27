package com.example.composefirsttry.giftcard.logic.stores.network

import com.example.composefirsttry.giftcard.logic.metadata.MetadataDbHelper
import com.example.composefirsttry.giftcard.logic.stores.network.sheet.SheetItem
import com.example.composefirsttry.giftcard.logic.stores.network.sheet.SheetsUsingUrl
import javax.inject.Inject

class RestGiftCardService @Inject constructor(private var metadataDbHelper: MetadataDbHelper) {
    fun getStores(): List<SheetItem> {
//        printAllFirebaseDB()
        val sheetItems = SheetsUsingUrl.dataFromWeb(metadataDbHelper.getCardsAmount(), metadataDbHelper.getStoresAmount())
        return orderItemsAlphabetically(sheetItems)
//        return mockDbValues()
    }

    //cards order: Max, Corporate, Hot
    private fun orderItemsAlphabetically(sheetItems: List<SheetItem>): List<SheetItem> {
       return sheetItems.sortedBy { it.storeName }
    }

//    private val NAME_PATTERN = Pattern.compile("""^[_A-z0-9]*((\s)*[_A-z0-9])*${'$'}""")
//    private fun isEnglishName(name: String): Boolean {
//        return NAME_PATTERN.matcher(name).matches()
//    }


//    private fun mockDbValues(): List<StoreServer> {
//        //Mock
//        //card order: Max, Corporate, Hot
//        val store1 = StoreServer("ACE", listOf(true, true, false))
//        val store2 = StoreServer("Adidas", listOf(true, false, true))
//        val store3 = StoreServer("afrodita", listOf(false, true, false))
//        val store4 = StoreServer("AMERICAN EAGLE", listOf(true, true, true))
//        val store5 = StoreServer("אדידס", listOf(true, false, true))
//        return listOf(store1, store2, store3, store4, store5)
//    }

/*    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    fun printAllFirebaseDB() {
        val dbRef = firebaseDatabase.reference
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("printAllFirebaseDB", "DB state:")
                    for (d in dataSnapshot.children) {
                        val key = d.key
                        val obj = d.value
                        Log.d("CARD", "($key : $obj)")
                    }
                }
            } //onDataChange

            override fun onCancelled(error: DatabaseError) {
                Log.d("printAllFirebaseDB", "onCancelled")
            } //onCancelled
        })
    }*/
}