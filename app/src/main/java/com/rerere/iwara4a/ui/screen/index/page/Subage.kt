package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@Composable
fun SubPage(navController: NavController, indexViewModel: IndexViewModel) {
    val subscriptionList = indexViewModel.subscriptionPager.collectAsLazyPagingItems()
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = subscriptionList.loadState.refresh == LoadState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            subscriptionList.loadState.refresh is LoadState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            subscriptionList.retry()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .padding(10.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(R.drawable.anime_1),
                                contentDescription = null
                            )
                        }
                        Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                    }
                }
            }
            else -> {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { subscriptionList.refresh() },
                    indicator = { s, trigger ->
                        SwipeRefreshIndicator(
                            s,
                            trigger,
                            contentColor = MaterialTheme.colors.onSurface
                        )
                    }) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (subscriptionList.loadState.refresh == LoadState.Loading && subscriptionList.itemCount == 0) {
                            items(6) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(16.dp)
                                        .placeholder(
                                            visible = true,
                                            highlight = PlaceholderHighlight.shimmer()
                                        )
                                )
                            }
                        }

                        items(subscriptionList) {
                            MediaPreviewCard(navController, it!!)
                        }

                        when (subscriptionList.loadState.append) {
                            LoadState.Loading -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(Modifier.size(30.dp))
                                        Text(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            text = "加载中..."
                                        )
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .noRippleClickable { subscriptionList.retry() }
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(140.dp)
                                                    .padding(10.dp)
                                                    .clip(CircleShape)
                                            ) {
                                                Image(
                                                    modifier = Modifier.fillMaxSize(),
                                                    painter = painterResource(R.drawable.anime_2),
                                                    contentDescription = null
                                                )
                                            }
                                            Text(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                text = "加载失败: ${(subscriptionList.loadState.append as LoadState.Error).error.message}"
                                            )
                                            Text(text = "点击重试")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}