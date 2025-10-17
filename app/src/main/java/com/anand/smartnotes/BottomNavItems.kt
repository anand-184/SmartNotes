package com.anand.smartnotes

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItems<T:Any>(val name:String, val route:T,val icon: ImageVector)

