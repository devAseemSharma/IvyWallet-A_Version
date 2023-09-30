package com.ivy.core.domain.action.algorithm

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.rawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.Clock
import java.time.Instant

class RawStatsKtTest {

    private lateinit var list: ArrayList<CalcTrn>


    @BeforeEach
    fun setup() {
        list = ArrayList()
    }


    @Test
    fun `check expense and income total`() {

        list.addAll(listOf(
            CalcTrn(20.0, "USD", TransactionType.Expense, Instant.now()),
            CalcTrn(40.0, "USD", TransactionType.Income, Instant.now()),
            CalcTrn(50.0, "USD", TransactionType.Expense, Instant.now()),
            CalcTrn(60.0, "USD", TransactionType.Income, Instant.now()),
            CalcTrn(70.0, "USD", TransactionType.Expense, Instant.now()),
        ))

        val rawStats = rawStats(list)

        assertThat(rawStats.incomes["USD"]).isEqualTo(100.0)
        assertThat(rawStats.expenses["USD"]).isEqualTo(140.0)
        assertThat(rawStats.incomes).isEqualTo(mapOf("USD" to 100.0))

    }

    @Test
    fun `check total income and expense count`(){
        list.addAll(listOf(
            CalcTrn(20.0, "USD", TransactionType.Expense, Instant.now()),
            CalcTrn(40.0, "USD", TransactionType.Income, Instant.now()),
            CalcTrn(50.0, "USD", TransactionType.Expense, Instant.now()),
            CalcTrn(60.0, "USD", TransactionType.Income, Instant.now()),
            CalcTrn(70.0, "USD", TransactionType.Expense, Instant.now()),
        ))

        val rawStats = rawStats(list)

        assertThat(rawStats.incomesCount).isEqualTo(2)
        assertThat(rawStats.expensesCount).isEqualTo(3)
    }


    @Test
    fun `check if the newest transaction time is the lowest`(){
        val initialTime = Instant.now(Clock.systemUTC())
        list.addAll(listOf(
            CalcTrn(20.0, "USD", TransactionType.Expense, initialTime),
            CalcTrn(40.0, "USD", TransactionType.Income, initialTime.plusSeconds(2)),
            CalcTrn(50.0, "USD", TransactionType.Expense, initialTime.plusSeconds(4)),
            CalcTrn(60.0, "USD", TransactionType.Income, initialTime.plusSeconds(6)),
            CalcTrn(70.0, "USD", TransactionType.Expense, initialTime.plusSeconds(8)),
        ))

        val rawStats = rawStats(list)

        assertThat(rawStats.newestTrnTime).isEqualTo(initialTime.plusSeconds(8))
    }
}