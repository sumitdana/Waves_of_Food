package com.example.wavesoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.ActivityLoginBinding
import com.example.wavesoffood.databinding.CartItrmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(private val cartItems:MutableList<String>,
                  private val cartItemsPrice:MutableList<String>,
                  private val cartImages:MutableList<String>,
                  private val cartDescription:MutableList<String>,
    private val cartQuantity:MutableList<Int>,
    private val context:Context):

    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
        private val auth=FirebaseAuth.getInstance()
    init {
        val database=FirebaseDatabase.getInstance()
        val userId=auth.currentUser?.uid?:""
        val cartItemNumber=cartItems.size

        itemQuantities=IntArray(cartItemNumber){1}
        cartItemReference=database.reference.child("user").child(userId).child("CartItems")
    }
    companion object{
        private var itemQuantities:IntArray= intArrayOf()
        private lateinit var cartItemReference:DatabaseReference
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItrmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = cartItems.size

    inner class CartViewHolder(private val binding: CartItrmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {

                val quantity=itemQuantities[position]
                cartfoodname.text=cartItems[position]
                cartitemprice.text=cartItemsPrice[position]

                //load Image using glide
                val uriString=cartImages[position]
                val uri=Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartimage)
                cartItemQuantity.text=quantity.toString()

                minusbutton.setOnClickListener {

                    decreasequantity(position)

                }

                plusbutton.setOnClickListener {
                    increasequantity(position)

                }
                deletebutton.setOnClickListener {
                    val itemposition=adapterPosition
                    if (itemposition!=RecyclerView.NO_POSITION){
                        deleteitems(itemposition)
                    }

                }

            }

        }

        private fun increasequantity(position: Int){
            if (itemQuantities[position]<10){
                itemQuantities[position]++
                binding.cartItemQuantity.text=itemQuantities[position].toString()
            }
        }

        private fun decreasequantity(position: Int){
            if (itemQuantities[position]>1){
                itemQuantities[position]--
                binding.cartItemQuantity.text=itemQuantities[position].toString()
            }
        }

        private fun deleteitems(position: Int){
//            cartItems.removeAt(position)
//            cartImages.removeAt(position)
//            cartItemsPrice.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position,cartItems.size)

            val positionRetrieve=position
            getUniqueKeyAtPosition(positionRetrieve){uniqueKey->
                if (uniqueKey!=null){
                    removeItem(position,uniqueKey)
                }

            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey!=null){
                cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    cartItems.removeAt(position)
                    cartImages.removeAt(position)
                    cartDescription.removeAt(position)
                    cartItemsPrice.removeAt(position)
                    cartQuantity.removeAt(position)

                    itemQuantities= itemQuantities.filterIndexed{index, i -> index!=position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,cartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to Remove", Toast.LENGTH_SHORT).show()

                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String) -> Unit) {
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey?:"")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }


    }



}