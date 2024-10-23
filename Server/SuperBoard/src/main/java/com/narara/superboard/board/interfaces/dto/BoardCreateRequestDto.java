package com.narara.superboard.board.interfaces.dto;

import java.util.Map;

public record BoardCreateRequestDto(String name,
                                    String visibility,
                                    Map<String, Object> background) implements BoardNameHolder {
}
