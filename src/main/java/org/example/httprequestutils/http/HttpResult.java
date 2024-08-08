package com.shj.steam_gui.utils.http;


import com.shj.steam_gui.utils.http.entity.RequestEntity;
import com.shj.steam_gui.utils.http.entity.ResponseEntity;

@FunctionalInterface
public interface HttpResult {

    ResponseEntity doResult(RequestEntity request, ResponseEntity response);

}
