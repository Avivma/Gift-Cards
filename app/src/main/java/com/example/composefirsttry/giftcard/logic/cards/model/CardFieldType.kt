package com.example.composefirsttry.giftcard.logic.cards.model

sealed interface CardFieldType {
    object Image : CardFieldType
    object Name : CardFieldType
    object Discount : CardFieldType
    object Number : CardFieldType
    object Cvv : CardFieldType
    object ExpirationDate : CardFieldType
}