package com.example.wavesoffood

import android.net.Uri
import android.opengl.GLU
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.ActivityFoodDetailsBinding

class FoodDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailsBinding

    private var foodName:String?=null
    private var foodImage:String?=null
    private var foodDescription:String?=null
    private var foodPrice:String?=null
    private var foodIngredients:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFoodDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        foodName=intent.getStringExtra("MenuItemName")
        foodPrice=intent.getStringExtra("MenuItemPrice")
        foodDescription=intent.getStringExtra("MenuItemDescription")
        foodIngredients=intent.getStringExtra("MenuItemIngredients")
        foodImage=intent.getStringExtra("MenuItemImage")

        Glide.with(this@FoodDetailsActivity)
            .load(Uri.parse(foodImage))
            .into(binding.detailfoodimage)

        with(binding){
            detailFoodName.text=foodName
            detailfooddescription.text=foodDescription
            detailfoodingredients.text=foodIngredients



        }

//        val foodName=intent.getStringExtra("MenuItemName")
//        val foodImage=intent.getIntExtra("MenuImage", 0)
//
//        binding.detailFoodName.text=foodName
//        binding.detailFoodImageView.setImageResource(foodImage)

        binding.imageButton.setOnClickListener{
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}