package com.covidroomtracker.services;

import com.covidroomtracker.entities.MeetingsEntity;
import com.covidroomtracker.entities.UserEntity;
import com.covidroomtracker.models.Meeting;
import com.covidroomtracker.repositories.MeetingsRepository;
import com.covidroomtracker.repositories.ReportRepository;
import com.covidroomtracker.repositories.UserRepository;
import org.hibernate.criterion.CriteriaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.sql.Timestamp;

@Service
public class MeetingsService {

    @Autowired
    private MeetingsRepository meetingsRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    public Boolean getMeetingContactTracing(String userId){
        try{
            List<MeetingsEntity> organizationMeetings = meetingsRepository.findPriorUsersMeetings(userId);
            if(organizationMeetings.isEmpty()) return false;

            List<String> usersInteracted = new ArrayList<String>();
            for(MeetingsEntity meeting : organizationMeetings){
                usersInteracted.add(meetingsRepository.findUsersInMeetingRooms(meeting, meeting.getMeeting_date()));
            }
            if(usersInteracted.size() <= 0 ) return false;

            return reportRepository.getLastUpdated(usersInteracted).size() > 0;
        }catch (Exception ex){
            return false;
        }
    }

    public void insertMeetings(List<MeetingsEntity> meetings){

        try{
            List<String> midList = new ArrayList<String>();
            meetings.forEach((meeting) -> midList.add(meeting.getMid()));

            List<String> existingMeetings = meetingsRepository.getExistingMeetings(midList);

            List<String> meetingsIds = new ArrayList<String>();
            for(MeetingsEntity entity: meetings){
               meetingsIds.add(entity.getMid());
            }

            meetingsIds.remove(existingMeetings);
            List<MeetingsEntity> goodMeetings = new ArrayList<MeetingsEntity>();
            for(MeetingsEntity entity: meetings){
                if(meetingsIds.contains(entity.getMid())){
                    goodMeetings.add(entity);
                }
            }
            
            
            meetingsRepository.saveAll(goodMeetings);
        }catch(Exception ex){

        }
    }

    public MeetingsEntity getUserMeetings(String userId){
        return meetingsRepository.getUserMeetings(userId);
    }

}
