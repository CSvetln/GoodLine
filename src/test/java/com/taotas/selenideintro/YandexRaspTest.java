package com.taotas.selenideintro;


import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.IntStream;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selectors.*;


class YandexRaspTest {

    @BeforeEach
    void openRasp() {
        open("https://rasp.yandex.ru");
    }

    void settingValues(String from, String to, String when) {
        $(byId("from")).setValue(from);
        $(byId("to")).setValue(to);
        $(byId("when")).setValue(when);
    }

    String encode(String str) {
        return new String(str.getBytes(), StandardCharsets.UTF_8);
    }

    @Test
    void searchTransportDirection() {
        String from = encode("Кемерово");
        String to = encode("Москва");
        String when = encode("7 июля");

        settingValues(from, to, when);

        $(By.className("SearchForm__submit")).pressEnter();

        IntStream.range(0, $$(By.className("SearchSegment")).size()).forEach(i -> {
            $$(By.className("SearchSegments")).get(i).find(byClassName("SegmentTitle__title")).exists();
            $$(By.className("SearchSegments")).get(i).find(byClassName("SearchSegment__duration")).exists();
            $$(By.className("SearchSegments")).get(i).find(byClassName("TransportIcon__icon")).exists();
        });

        $$(By.className("SearchSegment")).shouldHave(size(3));
    }

    @Test
    void searchTransportDirectionNo() {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextWednesday = currentDate.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

        String from = encode("Кемерово проспект Ленина");
        String to = encode("Кемерово Бакинский переулок");
        String when = encode(nextWednesday.toString());

        settingValues(from, to, when);

        $(byValue("bus")).parent().click();

        $(By.className("SearchForm__submit")).pressEnter();

        $(By.className("ErrorPageSearchForm__title"))
                .shouldHave(Condition.text(encode("Пункт отправления не найден. Проверьте правильность написания или выберите другой город.")));
    }
}
