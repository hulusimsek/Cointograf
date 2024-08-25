import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshPage(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    // Pull-to-refresh durumu
    val pullToRefreshState = rememberPullToRefreshState()
    val contentModifier = Modifier
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            // fetch something
            onRefresh()
            pullToRefreshState.endRefresh()
        }
    }
    // İçeriği göster

    // İçeriği güncellemek için LaunchedEffect kullanıyoruz
    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        content(contentModifier)
        PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))

//    or

//    if (state.isRefreshing) {
//        LinearProgressIndicator()
//    } else {
//        LinearProgressIndicator(progress = { state.progress })
//    }
    }

}