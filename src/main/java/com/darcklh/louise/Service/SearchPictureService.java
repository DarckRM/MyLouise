package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Messages.InMessage;
import org.springframework.stereotype.Service;

/**
 * @author DarckLH
 * @date 2022/8/13 0:02
 * @Description
 */
@Service
public interface SearchPictureService {
    public void findWithSourceNAO(InMessage inMessage, String url);
    public void findWithAscii2d(InMessage inMessage, String url);
}
