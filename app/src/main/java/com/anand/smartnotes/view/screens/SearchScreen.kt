package com.anand.smartnotes.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText

@Composable
fun SearchScreen(){
    Box(modifier = Modifier.fillMaxSize().background(DeepNavy)){
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightText)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var isChecked by remember { mutableStateOf(false) }

                TextButton(
                    onClick = { /* handle upload */ },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "📸 Upload Photo",
                        color = DeepNavy,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }

                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = DeepNavy,
                        uncheckedColor = DeepNavy
                    )
                )
            }

        }
    }
}

@Preview
@Composable
fun SearchScreenPreview(){
    SearchScreen()
}
