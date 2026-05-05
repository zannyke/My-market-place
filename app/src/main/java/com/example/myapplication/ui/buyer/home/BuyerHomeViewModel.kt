package com.example.myapplication.ui.buyer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Product
import com.example.myapplication.data.repository.ProductRepository
import kotlinx.coroutines.flow.*

import kotlinx.coroutines.launch

class BuyerHomeViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    val categories: List<Category> = ProductRepository.categories

    init {
        viewModelScope.launch {
            ProductRepository.seedDatabase()
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val filteredProducts: StateFlow<List<Product>> = combine(
        _searchQuery,
        _selectedCategoryId
    ) { query, categoryId ->
        Pair(query, categoryId)
    }.flatMapLatest { (query, categoryId) ->
        ProductRepository.filterProducts(query, categoryId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelect(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }
}
