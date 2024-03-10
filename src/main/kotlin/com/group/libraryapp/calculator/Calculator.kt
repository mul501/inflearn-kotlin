package com.group.libraryapp.calculator

class Calculator(
    var number: Int
) {

    fun add(operand: Int) {
        this.number += operand
    }

    fun minus(operand: Int) {
        this.number -= operand
    }

    fun multiply(operand: Int) {
        this.number *= operand
    }

    fun divide(operand: Int) {
        if (operand == 0) {
            throw IllegalArgumentException("Cannot divide by zero")
        }
        this.number /= operand
    }

}