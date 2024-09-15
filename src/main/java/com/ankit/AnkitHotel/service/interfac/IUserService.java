package com.ankit.AnkitHotel.service.interfac;

import com.ankit.AnkitHotel.dto.LoginRequest;
import com.ankit.AnkitHotel.dto.Response;
import com.ankit.AnkitHotel.entity.User;

public interface IUserService {
    Response register(User user);
    Response login(LoginRequest loginRequest);
    Response getAllUsers();
    Response getUserBookingHistory(String userId);
    Response deleteUser(String userId);
    Response getUserById(String userId);
    Response getMyInfo(String email);

}
