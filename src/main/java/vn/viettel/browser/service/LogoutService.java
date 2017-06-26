package vn.viettel.browser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.browser.model.MapDeviceAccModel;
import vn.viettel.browser.service.iface.LogoutServiceIface;

/**
 * Created by quytx on 4/24/2017.
 * Project: vn.viettel.browser.service:Social_Login
 */

@Service
public class LogoutService implements LogoutServiceIface {

    private final
    AccountService accountService;

    @Autowired
    public LogoutService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Long logout(String accessToken) {
        MapDeviceAccModel mapDeviceAccModel = accountService.findByAccessToken(accessToken);
        if (mapDeviceAccModel.getAccountName() == null) return 0L;
        mapDeviceAccModel.setAccessToken("");
        accountService.updateMapDeviceAccount(mapDeviceAccModel);
        return 1L;
    }
}
