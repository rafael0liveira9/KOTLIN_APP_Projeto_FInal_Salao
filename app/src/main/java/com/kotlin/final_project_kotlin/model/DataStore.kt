package com.kotlin.final_project_kotlin.model

import com.kotlin.final_project_kotlin.R

object DataStore {
    val ourServicesList: MutableList<Services> = arrayListOf()

    init {
        ourServicesList.add(Services(1,R.drawable.corte,"Corte e Lavagem","Serviço de Corte e Lavagem do Cabelo"))
        ourServicesList.add(Services(2,R.drawable.hidratacao,"Hidratação","Serviço de Lavagem e Hidratação do Cabelo"))
        ourServicesList.add(Services(3,R.drawable.progressiva,"Progressiva","Serviço de Aplicação Quimica de Progressiva no Cabelo"))
        ourServicesList.add(Services(4,R.drawable.tinturatotal,"Pigmentação Total","Serviço Pitura, total ou quase total do Cabelo"))
        ourServicesList.add(Services(5,R.drawable.tinturaparcial,"Pigmentação Parcial","Serviço Pitura, parcial do Cabelo"))
        ourServicesList.add(Services(6,R.drawable.maos,"Unhas das Mãos","Serviço Corte, Pitura e acabamento das unhas das mãos."))
        ourServicesList.add(Services(7,R.drawable.pes,"Unhas das Pés","Serviço Corte, Pitura e acabamento das unhas dos pés."))
        ourServicesList.add(Services(8,R.drawable.pesemaos,"Unhas Mãos e Pés","Serviço Corte, Pitura e acabamento das unhas das mãos e dos pés."))
    }

    fun getService(position: Int): Services? {
        return if (position >= 0 && position < ourServicesList.size) {
            ourServicesList[position]
        } else {
            null
        }
    }

    fun getServices(): List<Services> {
        return ourServicesList
    }
}