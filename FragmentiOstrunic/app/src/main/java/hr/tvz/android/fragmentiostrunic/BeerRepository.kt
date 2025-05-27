package hr.tvz.android.fragmentiostrunic

import android.util.Log
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BeerRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://fragmenti-ostrunic-default-rtdb.europe-west1.firebasedatabase.app/").reference
    private val beersRef = database.child("beers")

    companion object {
        private const val TAG = "BeerRepository"
    }

    // Spremi pivo u Firebase
    fun saveBeer(beer: Beer, onComplete: (Boolean) -> Unit) {
        val beerMap = mapOf(
            "name" to beer.name,
            "style" to beer.style,
            "abv" to beer.abv,
            "imageResId" to beer.imageResId,
            "websiteUrl" to beer.websiteUrl
        )

        beersRef.child(beer.name.replace(" ", "_")).setValue(beerMap)
            .addOnSuccessListener {
                Log.d(TAG, "Beer saved successfully: ${beer.name}")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save beer: ${beer.name}", exception)
                onComplete(false)
            }
    }

    // Dohvati sva piva iz Firebase
    fun getAllBeers(): Flow<List<Beer>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val beerList = mutableListOf<Beer>()

                for (childSnapshot in snapshot.children) {
                    try {
                        val name = childSnapshot.child("name").getValue(String::class.java) ?: ""
                        val style = childSnapshot.child("style").getValue(String::class.java) ?: ""
                        val abv = childSnapshot.child("abv").getValue(Double::class.java) ?: 0.0
                        val imageResId = childSnapshot.child("imageResId").getValue(Int::class.java) ?: R.drawable.ic_notification
                        val websiteUrl = childSnapshot.child("websiteUrl").getValue(String::class.java) ?: ""

                        val beer = Beer(name, style, abv, imageResId, websiteUrl)
                        beerList.add(beer)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing beer data", e)
                    }
                }

                Log.d(TAG, "Retrieved ${beerList.size} beers from Firebase")
                trySend(beerList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase read cancelled", error.toException())
                close(error.toException())
            }
        }

        beersRef.addValueEventListener(listener)

        awaitClose {
            beersRef.removeEventListener(listener)
        }
    }

    // Dodaj početna piva u bazu (pozovi jednom)
    fun initializeDefaultBeers() {
        val defaultBeers = listOf(
            Beer("Ožujsko", "Lager", 5.3, R.drawable.ozujsko, "https://ozujsko.com/"),
            Beer("Kozel", "Lager", 4.6, R.drawable.kozel, "https://www.kozel.hr/"),
            Beer("Guinness", "Stout", 4.2, R.drawable.guinness, "https://www.guinness.com/")
        )

        defaultBeers.forEach { beer ->
            saveBeer(beer) { success ->
                Log.d(TAG, "Default beer ${beer.name} saved: $success")
            }
        }
    }
}
