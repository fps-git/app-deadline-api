package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.APITestHelper;
import ru.netology.data.DataBaseHelper;

public class TransferTest {

    String token;

    @BeforeAll
    static void getToken() {
        APITestHelper.login();
        APITestHelper.authentication();
    }

    @BeforeEach
    void restoreSUT() {
        //Восстанавливаем SUT (баланс карт) после предыдущих тестов. Делаем это перед тестом, т.к. если бы
        // мы это делали после теста, то при падении теста баланс бы не восстановился для следующего теста.
        DataBaseHelper.setCardBalance("5559 0000 0000 0001", "10000");
        DataBaseHelper.setCardBalance("5559 0000 0000 0002", "10000");
    }


    @Test
    void shouldTransferMoney() {
        int transferAmount = 6000;

        //Фиксируем баланс до перевода
        int firstCardAmountBefore = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0001"));
        int secondCardAmountBefore = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0002"));

        APITestHelper.moneyTransfer("5559 0000 0000 0001", "5559 0000 0000 0002", transferAmount);

        //Фиксируем баланс после перевода
        int firstCardAmountAfter = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0001"));
        int secondCardAmountAfter = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0002"));

        Assertions.assertEquals(firstCardAmountBefore - transferAmount, firstCardAmountAfter);
        Assertions.assertEquals(secondCardAmountBefore + transferAmount, secondCardAmountAfter);
    }

    @Test
    @DisplayName("Should not transfer money if destination card doesn't exist")
    void ShouldNotTransferIfNoSuchDestinationCard() {
        int transferAmount = 3000;

        int originCardAmountBefore = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0001"));

        APITestHelper.moneyTransfer("5559 0000 0000 0001", "5559 0000 0000 0010", transferAmount);

        int originCardAmountAfter = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0001"));

        Assertions.assertEquals(originCardAmountBefore, originCardAmountAfter);
    }

    @Test
    @DisplayName("Should not transfer money if origin card doesn't exist")
    void ShouldNotTransferIfNoSuchOriginCard() {
        int transferAmount = 6320;

        int destinationCardAmountBefore = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0001"));

        APITestHelper.moneyTransfer("5559 0000 0000 0020", "5559 0000 0000 0001", transferAmount);

        int destinationCardAmountAfter = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0001"));

        Assertions.assertEquals(destinationCardAmountBefore, destinationCardAmountAfter);
    }

    @Test
    @DisplayName("Should not transfer money if origin card amount is not enough")
    void ShouldNotTransferIfNotEnoughMoney() {
        //Установить сумму перевода больше 10000
        int transferAmount = 17000;

        int originCardAmountBefore = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0002"));

        APITestHelper.moneyTransfer("5559 0000 0000 0002", "5559 0000 0000 0001", transferAmount);

        int originCardAmountAfter = Integer.parseInt(APITestHelper.getCardAmount("5559 0000 0000 0002"));

        Assertions.assertEquals(originCardAmountBefore, originCardAmountAfter);
    }
}
