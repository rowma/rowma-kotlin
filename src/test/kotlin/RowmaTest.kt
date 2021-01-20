import com.rowma.rowma_kotlin.Rowma

class RowmaTest {
    @org.junit.jupiter.api.Test
    internal fun testConstructor() {
        Rowma("https://rowma.moriokalab.com", "abcde")
        Rowma("https://rowma.moriokalab.com")
        Rowma()
    }
}