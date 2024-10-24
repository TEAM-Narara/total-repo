package com.narara.superboard.common.exception.authority;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidAuthorityFormatException extends InvalidFormatException {

  public InvalidAuthorityFormatException() {
    super("권한");
  }
}