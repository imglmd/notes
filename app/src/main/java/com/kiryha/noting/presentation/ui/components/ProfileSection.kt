package com.kiryha.noting.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.R

@Composable
fun ProfileSection(
    navController: NavController
) {
    Column {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(40.dp))
        if (false){

            Image(
                painter = painterResource(R.drawable.profile_icon),
                contentDescription = null,
                modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally)
            )
            Text(
                "username",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 15.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .height(48.dp)
                    .fillMaxWidth().clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "imglmd",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(end = 20.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "email",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 15.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .height(48.dp)
                    .fillMaxWidth().clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "imglmd@example.com ",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(end = 20.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "edit profile",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable(
                        onClick = { TODO() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ).padding(horizontal = 15.dp, vertical = 10.dp)
                )
                Text(
                    "log out",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable(
                        onClick = { TODO() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ).padding(horizontal = 15.dp, vertical = 10.dp)
                )
            }
        } else {
            Text(
                "needed for cloud storage",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 15.dp, bottom = 5.dp)
            )
            Column(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            navController.popBackStack()
                        }
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 8.dp, horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.labelMedium,
                        color =  MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)

                        .clip(RoundedCornerShape(4.dp))
                        .clickable {  }
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 8.dp, horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log in",
                        style = MaterialTheme.typography.labelMedium,
                        color =  MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
