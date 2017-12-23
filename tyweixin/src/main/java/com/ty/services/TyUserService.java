package com.ty.services;

import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.dao.MenuPowerMapper;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class TyUserService extends CommonService {

    @Autowired
    private CommonMapper commonMapper;


    @Autowired
    private CacheManager cacheManager;

    public Page getUserPage(Integer pageNum) throws Exception {
        return this.commonPage("ty_user", "updateTime desc", pageNum, 10, new HashMap<>());
    }
    public List getAllUser(){
        return this.commonList("ty_user",null,null,null,null);
    }
}
