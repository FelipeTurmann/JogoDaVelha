package com.example.jogodavelha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jogodavelha.ui.theme.DARK_GRENN
import com.example.jogodavelha.ui.theme.GRENN
import com.example.jogodavelha.ui.theme.WHITE

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var jogoAtivo by remember { mutableStateOf(false) }

            if (jogoAtivo) {
                JogoDaVelha()
            } else {
                TelaInicial {
                    jogoAtivo = true // Inicia o jogo quando o botão é pressionado
                }
            }
        }
    }
}

@Composable
fun TelaInicial(navigateToJogo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Jogo da Velha",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = GRENN,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navigateToJogo() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Começar Jogo")
        }
    }
}

@Composable
fun JogoDaVelha() {

    var bloco by remember { mutableStateOf(List(9) { "" }) }
    var jogadorAtual by remember { mutableStateOf("X") }
    var vencedor by remember { mutableStateOf<String?>(null) }

    fun cliqueDoBloco(index: Int) {
        if (bloco[index] == "" && vencedor == null) {
            bloco = bloco.toMutableStateList().apply {
                this[index] = jogadorAtual
            }
            jogadorAtual = if (jogadorAtual == "X") "O" else "X"
            vencedor = verificarVencedor(bloco)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = vencedor?.let { "Vencedor foi o: $it" } ?: "Vez do jogador: $jogadorAtual",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                tint = GRENN,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        bloco = List(9) { "" }
                        jogadorAtual = "X"
                        vencedor = null
                    }
            )
        }

        Grade(bloco = bloco, cliqueDoBloco = ::cliqueDoBloco)
    }
}

@Composable
fun Grade(
    bloco: List<String>, cliqueDoBloco: (Int) -> Unit
) {
    Column {
        for (row in 0 until 3) {
            Row {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    Quadrado(value = bloco[index], onClick = { cliqueDoBloco(index) })
                }
            }
        }
    }
}

@Composable
fun Quadrado(
    value: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(WHITE)
            .border(2.dp, DARK_GRENN)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = if (value == "X") Color.Blue else if (value == "O") Color.Red else GRENN,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun verificarVencedor(
    bloco: List<String>
): String? {

    val combinacoesVencedoras = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    for (combinacao in combinacoesVencedoras) {
        val (a, b, c) = combinacao
        if (bloco[a] != "" && bloco[a] == bloco[b] && bloco[a] == bloco[c])
            return bloco[a]
    }
    return if (bloco.all { it.isNotEmpty() }) "DEU VELHA!" else null
}
