import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.res.painterResource
import com.hulusimsek.cryptoapp.R

@Composable
fun BottomNavigationBar(mainViewModel: MainViewModel, navController: NavHostController) {
    val currentTab by mainViewModel.selectedTab.collectAsState()

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .navigationBarsPadding() // Automatically adds padding based on system navigation bars
    ) {
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = {
                mainViewModel.onTabSelected(0)
                navController.navigate("crypto_list_screen")
            },
            label = { Text("List") },
            icon = { Icon(painterResource(id = R.drawable.baseline_home_24), contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.Blue,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = {
                mainViewModel.onTabSelected(1)
                navController.navigate("crypto_detail_screen/1/1000") // Example values
            },
            label = { Text("Detail") },
            icon = { Icon(painterResource(id = R.drawable.baseline_home_24), contentDescription = null) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Blue,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.Blue,
                unselectedTextColor = Color.Gray
            )
        )
    }
}
