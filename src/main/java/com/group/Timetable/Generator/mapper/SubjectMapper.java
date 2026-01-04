
package com.group.Timetable.Generator.mapper;

import com.group.Timetable.Generator.dto.SubjectDTO;
import com.group.Timetable.Generator.entities.Subjects;

public class SubjectMapper {


	
	
	public static SubjectDTO toDTO(Subjects s) {
	    if (s == null) return null;

	    return new SubjectDTO(
	        s.getSubId(),                           
	        s.getSubName(),                         
	        s.getSemester(),                        
	        s.getSubType(),                         
	        s.getSubCredit(),                       
	        s.getBranch(),                         
	        s.getCourses() != null ? 
	            s.getCourses().getCourseName() : null, 
	        s.getUser() != null ? 
	            s.getUser().getUsername() : null        
	    );
	}


}
