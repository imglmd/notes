package com.kiryha.noting.presentation.components

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.R
import com.kiryha.noting.presentation.navigation.LoginScreen
import com.kiryha.noting.presentation.navigation.RegistrationScreen
import com.kiryha.noting.presentation.screens.auth.AuthViewModel
import com.kiryha.noting.presentation.screens.auth.AuthState
import com.kiryha.noting.utils.NetworkChecker

@Composable
fun ProfileSection(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val networkChecker = remember { NetworkChecker(context) }
    val isOnline = networkChecker.isOnline()

    Column {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleLarge
        )
        if (!isOnline) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        text = "No internet connection",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Please connect to Wi-Fi or mobile data to access your profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Spacer(Modifier.height(40.dp))
            when (authState) {
                // Показываем загрузку при проверке статуса
                AuthState.Initial, AuthState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Пользователь аутентифицирован
                AuthState.Authenticated -> {
                    Image(
                        painter = painterResource(R.drawable.profile_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterHorizontally)
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
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(100))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = currentUser?.username ?: "Загрузка...",
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
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(100))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = currentUser?.email ?: "Загрузка...",
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
                            modifier = Modifier
                                .clickable(
                                    onClick = { },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                        )
                        Text(
                            "log out",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .clickable(
                                    onClick = { viewModel.signOut() }, // ИСПРАВЛЕНО: вызов метода
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                        )
                    }
                }

                // Пользователь НЕ аутентифицирован
                AuthState.Unauthenticated -> {
                    UnauthenticatedContent(navController)
                }

                // Ошибка аутентификации
                is AuthState.Error -> {
                    Column {
                        Text(
                            text = "Error: ${(authState as AuthState.Error).message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(15.dp)
                        )
                        UnauthenticatedContent(navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun UnauthenticatedContent(navController: NavController) {
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
                .clickable { navController.navigate(RegistrationScreen) }
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 8.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign up",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { navController.navigate(LoginScreen) }
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 8.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}