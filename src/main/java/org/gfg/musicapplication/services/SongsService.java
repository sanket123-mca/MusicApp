package org.gfg.musicapplication.services;

import org.gfg.musicapplication.model.Songs;
import org.gfg.musicapplication.model.User;
import org.gfg.musicapplication.repositories.SongCacheRepository;
import org.gfg.musicapplication.repositories.SongsRepository;
import org.gfg.musicapplication.repositories.UserCacheRepository;
import org.gfg.musicapplication.repositories.UserRepository;
import org.gfg.musicapplication.requests.SongAdditionRequest;
import org.gfg.musicapplication.response.GenericMessages;
import org.gfg.musicapplication.response.SongAdditionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SongsService {

    @Autowired
    private SongsRepository songsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongCacheRepository songCacheRepository;

    @Autowired
    private UserCacheRepository userCacheRepository;
    public SongAdditionResponse songAddition(SongAdditionRequest songAdditionRequest) {
        if(songsRepository.findBySongTitle(songAdditionRequest.getSongTitle()) != null){
            return SongAdditionResponse.builder().message(GenericMessages.ENTRYALREADYPRESENT.getMessage()).build();
        }
        Songs songInDb = songAdditionRequest.toSong();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user =  (User) authentication.getPrincipal();
        User userFromDb = null;
        if(userCacheRepository.get(user.getMail()) != null){
            userFromDb = userCacheRepository.get(user.getMail());
        }else {
            userFromDb = userRepository.findByMail(user.getMail());
        }

        if(userFromDb != null){
            songInDb.setCreatedBy(userFromDb);
        }else{
            return SongAdditionResponse.builder().message(GenericMessages.USERNOTPRESENTTOCREATESONG.getMessage()).build();
        }
        songCacheRepository.set(songInDb);
        try{
            songInDb = songsRepository.save(songInDb);
        }catch (Exception e){
            e.printStackTrace();
            return SongAdditionResponse.builder().message(GenericMessages.SONGADDITIONFAILURE.getMessage()).build();
        }
        return SongAdditionResponse.builder().
                message(GenericMessages.SONGADDITIONSUCCESS.getMessage()).
                songId(songInDb.getPk()).
                songPath("").
                build();
    }
}
