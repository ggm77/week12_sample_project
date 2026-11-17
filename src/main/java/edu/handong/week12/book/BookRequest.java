package edu.handong.week12.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title,

        @NotBlank(message = "저자는 비어있을 수 없습니다.")
        String author,

        @Min(value = 0, message = "가격은 음수가 될 수 없습니다.")
        int price
) {
}
