package com.narara.superboard.common.service;

import com.narara.superboard.common.entity.CustomUserDetails;

public interface IAuthenticationFacade {
    CustomUserDetails getAuthenticatedUser();
}
