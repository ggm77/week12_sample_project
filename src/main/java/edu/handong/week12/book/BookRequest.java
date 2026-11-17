package edu.handong.week12.book;

public record BookRequest(
        String title,
        String author,
        int price
) {
}
