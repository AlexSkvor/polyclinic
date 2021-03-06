package ru.alexskvortsov.policlinic

import org.junit.Test
import org.junit.Assert.*
import ru.alexskvortsov.policlinic.user_generators.UsersGenerator

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun generateUsers() {
        UsersGenerator().generate(5)
    }

    @Test
    fun generateProcedures(){
        ProceduresGenerator().generateProcedures()
    }
}
