package com.exam.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.exam.analytics.StudentAnalyticsCalculation;
import com.exam.dbfactory.ConnectionFactory;
import com.exam.dto.StudentAnalyticsReportDto;
import com.exam.dto.UserMaster;
import com.exam.examlogic.Question;

/**
 * Servlet implementation class TutorLogin
 */
@WebServlet("/TutorLogin")
public class TutorLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TutorLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		ArrayList<Question> list=new ArrayList<Question>();
		HttpSession session=request.getSession();
		String username=request.getParameter("username");
		String imageUrl="";
		String usertype="";
		String email="";
		String password="";
		String userIdSession="";
		String websiteurl="";
		
		String NhighestMarks="0";
		String NumberofStudents="0";
		String NumberofWebUsed="0";
		String NumberofQuestionAsked="0";
		String NumberofLoginTimes="0";
		String NumberofHighestStudents="0";
		
		
		int flagloginCorrect=0;
		  String userid="";
		Connection con=null;
		if(username.equals(""))
		{
	
	  
	    email=request.getParameter("name");
		String name="";
		password=request.getParameter("password");				
		con=ConnectionFactory.getCon();
		ResultSet rs=null;

		int result=0;
		
		String type=request.getParameter("type");
		
		if(type.equalsIgnoreCase("tutor"))
		{

			 result=1;
		}
		
		try
		{
		 String query="select * from user_mst where email=? and password=? and user_type='TUTOR' and flagBlock='1'"; 
		 PreparedStatement ps=con.prepareStatement(query);
		 ps.setString(1, email);
		 ps.setString(2, password);
		 
		 rs=ps.executeQuery();
		 while(rs.next())
		 {
			 
			 
			name=rs.getString("name");
			userid=rs.getString("u_id") ;
			imageUrl=rs.getString("imageUrl") ;
			usertype=rs.getString("user_type");
			flagloginCorrect=1;
		 }
		 
		 
		 
		 if(userid.equalsIgnoreCase(""))
		 {
			result=0; 
		 }

		 if(result!=0)
		 { 
			 
			 
			 
			 String query2="select * from tutorurl where u_id='"+userid+"'"; 
			 PreparedStatement ps2=con.prepareStatement(query2);
			 ResultSet rt=ps2.executeQuery();
			 
			 while(rt.next())
			 {
				websiteurl=rt.getString("website_url") ;
				 
			 }
			 
			 

			 session.setAttribute("email", email);
			 session.setAttribute("websiteurl", websiteurl);
			 session.setAttribute("user",name);
		     session.setAttribute("userid",userid);
		     session.setAttribute("imageUrl", imageUrl);
		     session.setAttribute("usertype",usertype);
		     list=getAllQuestions();
		     
		     
		     int k=0;
		      for(int i=0;i<list.size();i++)
		      {
		    	k++;  
		      }
		     request.setAttribute("qCount", k+"");
		     request.setAttribute("data", list);
		     
			List<StudentAnalyticsReportDto>  studentAnalyticslist=new ArrayList<StudentAnalyticsReportDto>();
			studentAnalyticslist=StudentAnalyticsCalculation.getStudentPerformanceReport(userid);
			
			NhighestMarks=studentAnalyticslist.get(0).getHighestmarksachived();
			NumberofStudents=studentAnalyticslist.get(0).getNoofstudents();
			NumberofWebUsed=studentAnalyticslist.get(0).getNooflogin();
			NumberofQuestionAsked=studentAnalyticslist.get(0).getNumberofquestionasked();
			NumberofLoginTimes=studentAnalyticslist.get(0).getNooflogintimes();
			NumberofHighestStudents=studentAnalyticslist.get(0).getNoofhighestStudents();
			
			request.setAttribute("NhighestMarks",NhighestMarks);
			request.setAttribute("NumberofStudents", NumberofStudents);
			request.setAttribute("NumberofWebUsed",NumberofWebUsed);
			request.setAttribute("NumberofQuestionAsked",NumberofQuestionAsked);
			request.setAttribute("NumberofLoginTimes", NumberofLoginTimes);;
			request.setAttribute("NumberofHighestStudents", NumberofHighestStudents);;
			
//----------------------------------------------------------------------------------------------------------------------- 
			  String chartname="No. of Students";
			  DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
			  List<String> studentsnumberanalyticts=new ArrayList<String>();
			  studentsnumberanalyticts=getAllStudents(con);
			  
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(0)), chartname,  "JAN" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(1)), chartname ,  "FEB" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(2)), chartname,  "MAR" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(3)),chartname , "APR" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(4)) ,chartname , "MAY" ); 
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(5)) ,chartname , "JUN" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(6)),chartname, "JUL" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(7)) , chartname , "AUG" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(8)), chartname, "SEP" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(9)) , chartname, "OCT" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(10)) , chartname, "NOV" );
		      line_chart_dataset.addValue( Integer.parseInt(studentsnumberanalyticts.get(11)) , chartname, "DEC" );

		      JFreeChart lineChartObject = ChartFactory.createLineChart(
		         "Students counts per month","Months",
		         "No of Students ",
		         line_chart_dataset,PlotOrientation.VERTICAL,
		         true,true,false);

		      int width = 640; /* Width of the image */
		      int height = 480; /* Height of the image */ 
		      
		      String appPath = request.getServletContext().getRealPath("/images/LineChart"+userid+".jpeg");
		      File lineChart = new File( appPath ); 
		      ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
			
//-----------------------------------------------------------------------------------------------------------------------
		      final String jan = "JAN";
		      final String feb = "FEB";
		      final String mar = "MAR";
		      final String apr = "APR";
		      final String may = "MAY";
		      final String jun = "JUN";
		      final String jul = "JUL";
		      final String aug = "aug";
		      final String sep = "sep";
		      final String oct = "oct";
		      final String nov = "NOV";
		      final String dec = "DEC";

		      final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		      
		      List<String> studentperformancelist=new ArrayList<String>();
		      studentperformancelist=getAllMeritStudentsNumbers(con);
		      

		      dataset.addValue(Integer.parseInt(studentperformancelist.get(0)) , jan, jan );
		      dataset.addValue( Integer.parseInt(studentperformancelist.get(1)) , feb , feb );
		      dataset.addValue(Integer.parseInt(studentperformancelist.get(2)) , mar , mar);
		      dataset.addValue( Integer.parseInt(studentperformancelist.get(3)) , apr , apr );
		      dataset.addValue( Integer.parseInt(studentperformancelist.get(4)) , may , may );
		      dataset.addValue( Integer.parseInt(studentperformancelist.get(5)) , jun , jun );
		      dataset.addValue(Integer.parseInt(studentperformancelist.get(6)), jul , jul );
		      dataset.addValue( Integer.parseInt(studentperformancelist.get(7)), aug , aug );
		      dataset.addValue( Integer.parseInt(studentperformancelist.get(8)), sep , sep );
		      dataset.addValue(  Integer.parseInt(studentperformancelist.get(9)) , oct , oct);
		      dataset.addValue(  Integer.parseInt(studentperformancelist.get(10)) , nov , nov );
		      dataset.addValue(  Integer.parseInt(studentperformancelist.get(11)) , dec , dec );

		    
		      JFreeChart barChart = ChartFactory.createBarChart(
		         "Number of Merit Students", 
		         "Months", "Number of Students", 
		         dataset,PlotOrientation.HORIZONTAL, 
		         true, true, false);
		         
		    width = 640; /* Width of the image */
		    height = 480; /* Height of the image */ 
		    
		    
		      String appPath2 = request.getServletContext().getRealPath("/images/BarChart"+userid+".jpeg");
		    
		    
		    
		      File BarChart = new File( appPath2 ); 
		      ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
		      
//----------------------------------------------------------------------------------------------------------------------------------------------		      
		      
		      
			  chartname="No. of Students";
			  DefaultCategoryDataset line_chart_dataset2 = new DefaultCategoryDataset();
			  List<String> studentsnumberanalytictsforwebaccess=new ArrayList<String>();
			  studentsnumberanalytictsforwebaccess= getAllStudentsNumberofWebsiteaccess(con);
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(0)), chartname,  "JAN" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(1)), chartname ,  "FEB" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(2)), chartname,  "MAR" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(3)),chartname , "APR" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(4)) ,chartname , "MAY" ); 
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(5)) ,chartname , "JUN" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(6)),chartname, "JUL" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(7)) , chartname ,"AUG" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(8)), chartname, "SEP" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(9)) , chartname, "OCT" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(10)) , chartname, "NOV" );
		      line_chart_dataset2.addValue( Integer.parseInt(studentsnumberanalytictsforwebaccess.get(11)) , chartname, "DEC" );

		      JFreeChart lineChartObject2 = ChartFactory.createLineChart(
		         "Count per month","Months",
		         "No of Students ",
		         line_chart_dataset2,PlotOrientation.HORIZONTAL,
		         true,true,false);

		      width = 640; /* Width of the image */
		      height = 480; /* Height of the image */ 
		      
		      String appPath3 = request.getServletContext().getRealPath("/images/LineChartWebAccess"+userid+".jpeg");
		      File lineChart2 = new File( appPath3 ); 
		      ChartUtilities.saveChartAsJPEG(lineChart2 ,lineChartObject2, width ,height);
		      
//----------------------------------------------------------------------------------------------------------------------------------------------------		      
		      
		      List<String> studentNoQuestionasked=new ArrayList<String>();
		      studentNoQuestionasked=getNumberofQuestionAsked(con);
		      
		      DefaultPieDataset dataset2 = new DefaultPieDataset( );             
		      dataset2.setValue( "Jan" , Integer.parseInt(studentNoQuestionasked.get(0)) );             
		      dataset2.setValue( "Feb" ,Integer.parseInt(studentNoQuestionasked.get(1)) );             
		      dataset2.setValue( "Mar" ,Integer.parseInt(studentNoQuestionasked.get(2)) );             
		      dataset2.setValue( "Apr" , Integer.parseInt(studentNoQuestionasked.get(3)) ); 
		      dataset2.setValue( "May" , Integer.parseInt(studentNoQuestionasked.get(4)) ); 
		      dataset2.setValue( "Jun" ,Integer.parseInt(studentNoQuestionasked.get(5)) ); 
		      dataset2.setValue( "Jul" ,Integer.parseInt(studentNoQuestionasked.get(6)) ); 
		      dataset2.setValue( "Aug" , Integer.parseInt(studentNoQuestionasked.get(7)) ); 
		      dataset2.setValue( "Sep" , Integer.parseInt(studentNoQuestionasked.get(8)) ); 
		      dataset2.setValue( "Oct" , Integer.parseInt(studentNoQuestionasked.get(9)) ); 
		      dataset2.setValue( "Nov" , Integer.parseInt(studentNoQuestionasked.get(10)) ); 
		      dataset2.setValue( "Dec" , Integer.parseInt(studentNoQuestionasked.get(11)) ); 

		      JFreeChart chart = ChartFactory.createPieChart3D( 
		         "No of Question asked to Tutors" ,  // chart title                   
		         dataset2 ,         // data 
		         true ,            // include legend                   
		         true, 
		         false);

		      final PiePlot3D plot = ( PiePlot3D ) chart.getPlot( );             
		      plot.setStartAngle( 270 );             
		      plot.setForegroundAlpha( 0.60f );             
		      plot.setInteriorGap( 0.02 );             
		      width = 640; /* Width of the image */             
		      height = 480; /* Height of the image */       
		      
		      
		      
		      
		      String appPath4 = request.getServletContext().getRealPath("/images/PieNquestion"+userid+".jpeg");
		  
		      
		      
		      File pieChart3D = new File(appPath4);                           
		      ChartUtilities.saveChartAsJPEG( pieChart3D , chart , width , height );  
		      
		     
			 RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/loginTutorsPanel.jsp");
			 rd.forward(request, response); 
		 }
		 else
		 {
			 if(email.equals("admin") && password.equals("12345"))
			 {
				 System.out.println("called")  ;
		   		  imageUrl=""; 
		   		List<UserMaster> studentList=new ArrayList<UserMaster>();
		   		List<UserMaster> tutorList=new ArrayList<UserMaster>();
		   		
		   	    con=ConnectionFactory.getCon();
		        username=request.getParameter("username");
				if(username.equals(""))
				{
			    userid="";
			     name=request.getParameter("name");
			     password=request.getParameter("password");				
				
			   rs=null;
			    result=0;
				try
				{
				 query="SELECT * FROM user_mst WHERE NAME=? AND PASSWORD=? AND user_type='ADMIN'"; 
				  ps=con.prepareStatement(query);
				 ps.setString(1, name);
				 ps.setString(2, password);
				 rs=ps.executeQuery();
				 System.out.println("Result of ps : "+ps);
				 while(rs.next())
				 {
					userid=rs.getString("u_id") ;
					imageUrl=rs.getString("imageUrl");
					usertype=rs.getString("user_type");
					userIdSession=rs.getString("u_id");
	
				 result=1;
				 }
				 if(result!=0)
				 { 
				     session.setAttribute("user",name);
				     session.setAttribute("userid",userid);
				     session.setAttribute("imageUrl",imageUrl);
				     session.setAttribute("usertype",usertype);
				     studentList=getAllStudentList(con);
				     tutorList=getAllTutorList(con);
				     
				     int numofstudents=getNumberOfStudents(con);
				     int numofTutors=getNumberOfTutors(con);
				     
				     request.setAttribute("numofstudents", numofstudents);
				     request.setAttribute("numofTutors", numofTutors);
				     
				     request.setAttribute("studentList", studentList);
				     request.setAttribute("tutorList", tutorList);
				     
					 RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/adminDashboard.jsp");
					 rd.forward(request, response); 
				 }
				 else
				 {
					 System.out.println("Login not found !!");
					 request.setAttribute("msg", "<b style=color:red>Sorry invalid login details found !!</b>");
					 RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/loginTutorsPanel.jsp");
					 rd.forward(request, response); 
				 }
				}catch(SQLException sqe){
					System.out.println("Error : While Fetching records from database");
					}
				}
				else
				{ 
					     studentList=getAllStudentList(con);
					     tutorList=getAllTutorList(con);
					    
					     int numofstudents=getNumberOfStudents(con);
					     int numofTutors=getNumberOfTutors(con);
					     
					     request.setAttribute("numofstudents", numofstudents);
					     request.setAttribute("numofTutors", numofTutors);
					     
					     request.setAttribute("studentList", studentList);
					     request.setAttribute("tutorList", tutorList);
					     
					     request.setAttribute("studentList", studentList);
					     request.setAttribute("tutorList", tutorList);
					     RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/adminDashboard.jsp");
					     rd.forward(request, response);     
				}
			 }
			 
			 session.setAttribute("userIdSession", userIdSession);

			 System.out.println("Login not found !!");
			 request.setAttribute("msg", "<b style=color:red>Sorry invalid login details found !!</b>");
			 RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/login.jsp");
			 rd.forward(request, response); 
		 }
		}catch(Exception sqe){
			System.out.println("Error : While Fetching records from database");
			
			
			if(flagloginCorrect>0)
			{
				
				
				     RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/loginTutorsPanel.jsp");
					 rd.forward(request, response); 
			}
			else
			{
				request.setAttribute("msg", "<b style=color:red>Sorry invalid login details found !!</b>");
				RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/login.jsp");
				 rd.forward(request, response); 
			}
			
			
			
			}
		}
		else
		{
		     list=getAllQuestions();
		     int k=0;
		      for(int i=0;i<list.size();i++)
		      {
		    	k++;  
		      }
			
			 request.setAttribute("qCount", k+"");
		     request.setAttribute("data", list);
			 RequestDispatcher rd=request.getRequestDispatcher("/WEB-INF/UI_Pages/loginTutorsPanel.jsp");
			 rd.forward(request, response); 
		}
	}
	
	public ArrayList<Question> getAllQuestions()
	{
		ArrayList<Question> ars=new ArrayList<Question>();
		Connection con=ConnectionFactory.getCon();
		try
		{
			String query2="SELECT q.q_id ,q.question, q.date,u.name,u.user_type,u.u_id,q.sub_id,u.imageUrl FROM question_mst q JOIN user_mst u WHERE q.u_id=u.u_id  And q.flagBlock='1' ";
		     PreparedStatement ps2=con.prepareStatement(query2);
		     ResultSet rs2=ps2.executeQuery();
		     int k=0;
		     while(rs2.next())
		     {
		    	 Question ob=new Question();
		    	 ob.setQuestion(rs2.getString("question"));
		    	 ob.setQuestionNumber(rs2.getInt("q_id"));
		    	 ob.setUsername(rs2.getString("name"));
		    	 ob.setQdate(rs2.getString("date"));
		    	 ob.setUserId(rs2.getString("u_id"));
		    	 ob.setUserType(rs2.getString("user_type"));
		    	 ob.setSub_id(rs2.getString("sub_id"));
		    	 ob.setImageUrl(rs2.getString("imageUrl"));
		    	 ob.setSub_type(getSubNameBySubId(Integer.parseInt(rs2.getString("sub_id")), con));
		    	 ars.add(ob);
		    	 k++;
		     }	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return ars;
	}
	
	public String getSubNameBySubId(int sub_id,Connection con)
	{
		String subname="";
		try
		{
			String query2="Select * from subject_mst where sub_id='"+sub_id+"' ";
			PreparedStatement ps2=con.prepareStatement(query2);
			 ResultSet rs2=ps2.executeQuery();
		     int k=0;
		     while(rs2.next())
		     {
		    	 subname=rs2.getString("subject_type") ;
		     }
			
		}
		catch(Exception e)
		{
			
			System.out.println("Exception occur"+e);
		}
		
		return subname;
	}
	
	public List<UserMaster> getAllStudentList(Connection con)
	{
		List<UserMaster> studentList=new ArrayList<UserMaster>();
		try
		{
			
		PreparedStatement pr=con.prepareStatement("select * from user_mst where user_type='STUDENT'")	;
		ResultSet rs=pr.executeQuery();
		while(rs.next())
		{
			UserMaster stnt=new UserMaster();
			stnt.setUid(rs.getString("u_id"));
			stnt.setNamne(rs.getString("name"));
			stnt.setFlagBlock(rs.getString("flagBlock"));
			stnt.setImageUrl(rs.getString("imageUrl"));
			studentList.add(stnt);
			
		}
		}
		catch(Exception e)
		{
			System.out.println("Exception occur" + e);
		}
		
		return studentList;
	}
	
	public List<UserMaster> getAllTutorList(Connection con)
	{
		List<UserMaster> studentList=new ArrayList<UserMaster>();
		try
		{
			
		PreparedStatement pr=con.prepareStatement("select * from user_mst where user_type='TUTOR'")	;
		ResultSet rs=pr.executeQuery();
		while(rs.next())
		{
			UserMaster stnt=new UserMaster();
			stnt.setUid(rs.getString("u_id"));
			stnt.setNamne(rs.getString("name"));
			stnt.setFlagBlock(rs.getString("flagBlock"));
			stnt.setImageUrl(rs.getString("imageUrl"));
			studentList.add(stnt);
		}
				
		}
		catch(Exception e)
		{
			System.out.println("Exception occur" + e);
		}
		
		return studentList;
	}
	
	
     public int getNumberOfStudents(Connection con)
     {
    	 int num=0;
    	try
    	{
    		PreparedStatement pt=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst where user_type='STUDENT'");
    		ResultSet rs=pt.executeQuery();
    		while(rs.next())
    		{
    		 num=rs.getInt("NoofStudents");
    		}
    	}
    	 catch(Exception e)
    	 {
    		 
    	System.out.println("Exception occur" + e);	 
    		 
    	 }
    	 
    	 return num;
    	 
     }
 	
     public int getNumberOfTutors(Connection con)
     {
    	 int num=0;
    	try
    	{
    		PreparedStatement pt=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst where user_type='TUTOR'");
    		ResultSet rs=pt.executeQuery();
    		while(rs.next())
    		{
    		 num=rs.getInt("NoofStudents");
    		 
    		}
    		
    	}
    	 catch(Exception e)
    	 {
    		 
    	System.out.println("Exception occur" + e);	 
    		 
    	 }
    	 
    	 return num;
    	 
     }
	
	public List<String> getAllStudents(Connection con) 
	{
		String year="";
		List<String> noofstudents=new ArrayList<String>();	
	  String janstudent="";
	  String febstudent="";
	  String marstudent="";
	  String aprstudent="";
	  String maystudent="";
	  String junestudent="";
	  String julystudent="";
	  String augStudent="";
	  String sepStudent="";
	  String octStudent="";
	  String novStudent="";
	  String decStudent="";
			  

		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int years = now.get(Calendar.YEAR);
		year=years+"";
		
		try
		{
		
		PreparedStatement psjan=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-01-01' AND '"+year+"-01-31'")	;
		ResultSet rsjan=psjan.executeQuery();
		while(rsjan.next())
		{
			janstudent=rsjan.getString("NoofStudents");
		}
			
		PreparedStatement psfeb=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-02-01' AND '"+year+"-02-30'")	;
		ResultSet rsfeb=psfeb.executeQuery();
		while(rsfeb.next())
		{
			febstudent=rsfeb.getString("NoofStudents");
		}	
			
			
		PreparedStatement psmar=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-03-01' AND '"+year+"-03-31'")	;
		ResultSet rsmar=psmar.executeQuery();
		while(rsmar.next())
		{
			marstudent=rsmar.getString("NoofStudents");
		}	
			
			
		PreparedStatement psapr=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-04-01' AND '"+year+"-04-31'")	;
		ResultSet rsapr=psapr.executeQuery();
		while(rsapr.next())
		{
			aprstudent=rsapr.getString("NoofStudents");
		}
		
		
		PreparedStatement psmay=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-05-01' AND '"+year+"-05-31'")	;
		ResultSet rsmay=psmay.executeQuery();
		while(rsmay.next())
		{
			maystudent=rsmay.getString("NoofStudents");
		}
		
		
		PreparedStatement psjun=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-06-01' AND '"+year+"-06-31'")	;
		ResultSet rsjun=psjun.executeQuery();
		while(rsjun.next())
		{
			junestudent=rsjun.getString("NoofStudents");
		}
		
		
		PreparedStatement psjul=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-07-01' AND '"+year+"-07-31'")	;
		ResultSet   rsjul=psjul.executeQuery();
		while(rsjul.next())
		{
			julystudent=rsjul.getString("NoofStudents");
		}
			
		
		PreparedStatement psaug=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-08-01' AND '"+year+"-08-31'")	;
		ResultSet   rsaug=psaug.executeQuery();
		while(rsaug.next())
		{
			augStudent=rsaug.getString("NoofStudents");
		}
			
		
		PreparedStatement pssep=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-09-01' AND '"+year+"-09-31'")	;
		ResultSet   rssep=pssep.executeQuery();
		while(rssep.next())
		{
			sepStudent=rssep.getString("NoofStudents");
		}
			
		

		PreparedStatement psoct=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-10-01' AND '"+year+"-10-31'")	;
		ResultSet   rsoct=psoct.executeQuery();
		while(rsoct.next())
		{
			octStudent=rsoct.getString("NoofStudents");
		}
		
		
		PreparedStatement psnov=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-11-01' AND '"+year+"-11-31'")	;
		ResultSet   rsnov=psnov.executeQuery();
		while(rsnov.next())
		{
			novStudent=rsnov.getString("NoofStudents");
		}
			

		PreparedStatement psdec=con.prepareStatement("SELECT COUNT(NAME) AS NoofStudents FROM user_mst WHERE DATE BETWEEN '"+year+"-06-01' AND '"+year+"-06-31'")	;
		ResultSet   rsdec=psdec.executeQuery();
		while(rsdec.next())
		{
			decStudent=rsdec.getString("NoofStudents");
		}
			
		
		noofstudents.add(janstudent);
		noofstudents.add(febstudent);
		noofstudents.add(marstudent);
		noofstudents.add(aprstudent);
		noofstudents.add(maystudent);
		noofstudents.add(junestudent);
		noofstudents.add(julystudent);
		noofstudents.add(augStudent);
		noofstudents.add(sepStudent);
		noofstudents.add(octStudent);
		noofstudents.add(novStudent);
		noofstudents.add(decStudent);
		
		}
		catch(Exception e)
		{
			System.out.println("Exception occur"+e);
		}
		
		return noofstudents;
		
	}
	
	
	public List<String> getAllStudentsNumberofWebsiteaccess(Connection con) 
	{
		String year="";
		List<String> noofstudents=new ArrayList<String>();	
	  String janstudent="";
	  String febstudent="";
	  String marstudent="";
	  String aprstudent="";
	  String maystudent="";
	  String junestudent="";
	  String julystudent="";
	  String augStudent="";
	  String sepStudent="";
	  String octStudent="";
	  String novStudent="";
	  String decStudent="";
			  
		
		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int years = now.get(Calendar.YEAR);
		year=years+"";
		

	
		try
		{
		
		PreparedStatement psjan=con.prepareStatement("SELECT COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-01-01' AND '"+year+"-01-31'")	;
		ResultSet rsjan=psjan.executeQuery();
		while(rsjan.next())
		{
			janstudent=rsjan.getString("NoofStudents");
		}
			
		PreparedStatement psfeb=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-02-01' AND '"+year+"-02-30'")	;
		ResultSet rsfeb=psfeb.executeQuery();
		while(rsfeb.next())
		{
			febstudent=rsfeb.getString("NoofStudents");
		}	
			
			
		PreparedStatement psmar=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-03-01' AND '"+year+"-03-31'")	;
		ResultSet rsmar=psmar.executeQuery();
		while(rsmar.next())
		{
			marstudent=rsmar.getString("NoofStudents");
		}	
			
			
		PreparedStatement psapr=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-04-01' AND '"+year+"-04-31'")	;
		ResultSet rsapr=psapr.executeQuery();
		while(rsapr.next())
		{
			aprstudent=rsapr.getString("NoofStudents");
		}
		
		
		PreparedStatement psmay=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN  '"+year+"-05-01' AND '"+year+"-05-31'")	;
		ResultSet rsmay=psmay.executeQuery();
		while(rsmay.next())
		{
			maystudent=rsmay.getString("NoofStudents");
		}
		
		
		PreparedStatement psjun=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN  '"+year+"-06-01' AND '"+year+"-06-31'")	;
		ResultSet rsjun=psjun.executeQuery();
		while(rsjun.next())
		{
			junestudent=rsjun.getString("NoofStudents");
		}
		
		
		PreparedStatement psjul=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-07-01' AND '"+year+"-07-31'")	;
		ResultSet   rsjul=psjul.executeQuery();
		while(rsjul.next())
		{
			julystudent=rsjul.getString("NoofStudents");
		}
			
		
		

		PreparedStatement psaug=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-08-01' AND '"+year+"-08-31'")	;
		ResultSet   rsaug=psaug.executeQuery();
		while(rsaug.next())
		{
			augStudent=rsaug.getString("NoofStudents");
		}
			
		
		

		PreparedStatement pssep=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN  '"+year+"-09-01' AND '"+year+"-09-31'")	;
		ResultSet   rssep=pssep.executeQuery();
		while(rssep.next())
		{
			sepStudent=rssep.getString("NoofStudents");
		}
			
		
		PreparedStatement psoct=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-10-01' AND '"+year+"-10-31'")	;
		ResultSet   rsoct=psoct.executeQuery();
		while(rsoct.next())
		{
			octStudent=rsoct.getString("NoofStudents");
		}
		
		
		PreparedStatement psnov=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-11-01' AND '"+year+"-11-31'")	;
		ResultSet   rsnov=psnov.executeQuery();
		while(rsnov.next())
		{
			novStudent=rsnov.getString("NoofStudents");
		}
			
		PreparedStatement psdec=con.prepareStatement("SELECT  COUNT(LoginTime) AS NoofStudents FROM logintimemaster WHERE LoginTime BETWEEN '"+year+"-06-01' AND '"+year+"-06-31'")	;
		ResultSet   rsdec=psdec.executeQuery();
		while(rsdec.next())
		{
			decStudent=rsdec.getString("NoofStudents");
		}
			
		noofstudents.add(janstudent);
		noofstudents.add(febstudent);
		noofstudents.add(marstudent);
		noofstudents.add(aprstudent);
		noofstudents.add(maystudent);
		noofstudents.add(junestudent);
		noofstudents.add(julystudent);
		noofstudents.add(augStudent);
		noofstudents.add(sepStudent);
		noofstudents.add(octStudent);
		noofstudents.add(novStudent);
		noofstudents.add(decStudent);
		
		}
		catch(Exception e)
		{
			System.out.println("Exception occur"+e);
		}
		
		return noofstudents;
		
	}
	

	public List<String> getAllMeritStudentsNumbers(Connection con) 
	{
		String year="";
		List<String> noofstudents=new ArrayList<String>();	
	  String janstudent="";
	  String febstudent="";
	  String marstudent="";
	  String aprstudent="";
	  String maystudent="";
	  String junestudent="";
	  String julystudent="";
	  String augStudent="";
	  String sepStudent="";
	  String octStudent="";
	  String novStudent="";
	  String decStudent="";
			  
		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int years = now.get(Calendar.YEAR);
		year=years+"";
		
		try
		{
		
		PreparedStatement psjan=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-01-01' AND '"+year+"-01-31' AND marksobtain>80 ")	;
		ResultSet rsjan=psjan.executeQuery();
		while(rsjan.next())
		{
			janstudent=rsjan.getString("NoofStudents");
		}
			
		PreparedStatement psfeb=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-02-01' AND '"+year+"-02-31' AND marksobtain>80")	;
		ResultSet rsfeb=psfeb.executeQuery();
		while(rsfeb.next())
		{
			febstudent=rsfeb.getString("NoofStudents");
		}	
			
			
		PreparedStatement psmar=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-03-01' AND '"+year+"-03-31' AND marksobtain>80")	;
		ResultSet rsmar=psmar.executeQuery();
		while(rsmar.next())
		{
			marstudent=rsmar.getString("NoofStudents");
		}	
			
			
		PreparedStatement psapr=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-04-01' AND '"+year+"-04-31' AND marksobtain>80")	;
		ResultSet rsapr=psapr.executeQuery();
		while(rsapr.next())
		{
			aprstudent=rsapr.getString("NoofStudents");
		}
		
		
		PreparedStatement psmay=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-05-01' AND '"+year+"-05-31' AND marksobtain>80")	;
		ResultSet rsmay=psmay.executeQuery();
		while(rsmay.next())
		{
			maystudent=rsmay.getString("NoofStudents");
		}
		
		
		PreparedStatement psjun=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-06-01' AND '"+year+"-06-31' AND marksobtain>80")	;
		ResultSet rsjun=psjun.executeQuery();
		while(rsjun.next())
		{
			junestudent=rsjun.getString("NoofStudents");
		}
		
		
		PreparedStatement psjul=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-07-01' AND '"+year+"-07-31' AND marksobtain>80")	;
		ResultSet   rsjul=psjul.executeQuery();
		while(rsjul.next())
		{
			julystudent=rsjul.getString("NoofStudents");
		}
			
		
		PreparedStatement psaug=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-08-01' AND '"+year+"-08-31' AND marksobtain>80")	;
		ResultSet   rsaug=psaug.executeQuery();
		while(rsaug.next())
		{
			augStudent=rsaug.getString("NoofStudents");
		}
			
		
		PreparedStatement pssep=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-09-01' AND '"+year+"-09-31' AND marksobtain>80")	;
		ResultSet   rssep=pssep.executeQuery();
		while(rssep.next())
		{
			sepStudent=rssep.getString("NoofStudents");
		}
			

		PreparedStatement psoct=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-10-01' AND '"+year+"-10-31' AND marksobtain>80")	;
		ResultSet   rsoct=psoct.executeQuery();
		while(rsoct.next())
		{
			octStudent=rsoct.getString("NoofStudents");
		}
		
		
		PreparedStatement psnov=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-11-01' AND '"+year+"-11-31' AND marksobtain>80")	;
		ResultSet   rsnov=psnov.executeQuery();
		while(rsnov.next())
		{
			novStudent=rsnov.getString("NoofStudents");
		}
			
	
		PreparedStatement psdec=con.prepareStatement("Select COUNT(dateofexam) AS NoofStudents FROM studentexamansmaster WHERE dateofexam BETWEEN '"+year+"-12-01' AND '"+year+"-12-31' AND marksobtain>80")	;
		ResultSet   rsdec=psdec.executeQuery();
		while(rsdec.next())
		{
			decStudent=rsdec.getString("NoofStudents");
		}
			
		
		noofstudents.add(janstudent);
		noofstudents.add(febstudent);
		noofstudents.add(marstudent);
		noofstudents.add(aprstudent);
		noofstudents.add(maystudent);
		noofstudents.add(junestudent);
		noofstudents.add(julystudent);
		noofstudents.add(augStudent);
		noofstudents.add(sepStudent);
		noofstudents.add(octStudent);
		noofstudents.add(novStudent);
		noofstudents.add(decStudent);
		
	
		}
		catch(Exception e)
		{
			System.out.println("Exception occur"+e);
		}
		

		return noofstudents;
		
	}
	

	public List<String> getNumberofQuestionAsked(Connection con) 
	{
	  String year="";
	  List<String> noofstudents=new ArrayList<String>();	
	  String janstudent="";
	  String febstudent="";
	  String marstudent="";
	  String aprstudent="";
	  String maystudent="";
	  String junestudent="";
	  String julystudent="";
	  String augStudent="";
	  String sepStudent="";
	  String octStudent="";
	  String novStudent="";
	  String decStudent="";
			  
	
		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int years = now.get(Calendar.YEAR);
		year=years+"";
		
	
		try
		{
		
		PreparedStatement psjan=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-01-01' AND '"+year+"-01-31'")	;
		ResultSet rsjan=psjan.executeQuery();
		while(rsjan.next())
		{
			janstudent=rsjan.getString("NoofStudents");
		}
			
		PreparedStatement psfeb=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN  '"+year+"-02-01' AND '"+year+"-02-31' ")	;
		ResultSet rsfeb=psfeb.executeQuery();
		while(rsfeb.next())
		{
			febstudent=rsfeb.getString("NoofStudents");
		}	
			
			
		PreparedStatement psmar=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-03-01' AND '"+year+"-03-31' ")	;
		ResultSet rsmar=psmar.executeQuery();
		while(rsmar.next())
		{
			marstudent=rsmar.getString("NoofStudents");
		}	
			
			
		PreparedStatement psapr=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN  '"+year+"-04-01' AND '"+year+"-04-31'")	;
		ResultSet rsapr=psapr.executeQuery();
		while(rsapr.next())
		{
			aprstudent=rsapr.getString("NoofStudents");
		}
		
		
		PreparedStatement psmay=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-05-01' AND '"+year+"-05-31' ")	;
		ResultSet rsmay=psmay.executeQuery();
		while(rsmay.next())
		{
			maystudent=rsmay.getString("NoofStudents");
		}
		
		
		PreparedStatement psjun=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-06-01' AND '"+year+"-06-31' ")	;
		ResultSet rsjun=psjun.executeQuery();
		while(rsjun.next())
		{
			junestudent=rsjun.getString("NoofStudents");
		}
		
		
		PreparedStatement psjul=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-07-01' AND '"+year+"-07-31' ")	;
		ResultSet   rsjul=psjul.executeQuery();
		while(rsjul.next())
		{
			julystudent=rsjul.getString("NoofStudents");
		}
			
		
		PreparedStatement psaug=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-08-01' AND '"+year+"-08-31' ")	;
		ResultSet   rsaug=psaug.executeQuery();
		while(rsaug.next())
		{
			augStudent=rsaug.getString("NoofStudents");
		}
			
		
		PreparedStatement pssep=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-09-01' AND '"+year+"-09-31' ")	;
		ResultSet   rssep=pssep.executeQuery();
		while(rssep.next())
		{
			sepStudent=rssep.getString("NoofStudents");
		}
			
		

		PreparedStatement psoct=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN  '"+year+"-10-01' AND '"+year+"-10-31'")	;
		ResultSet   rsoct=psoct.executeQuery();
		while(rsoct.next())
		{
			octStudent=rsoct.getString("NoofStudents");
		}
		
		
		PreparedStatement psnov=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN  '"+year+"-11-01' AND '"+year+"-11-31' ")	;
		ResultSet   rsnov=psnov.executeQuery();
		while(rsnov.next())
		{
			novStudent=rsnov.getString("NoofStudents");
		}
			
		
		PreparedStatement psdec=con.prepareStatement("Select COUNT(DATE) AS NoofStudents FROM question_mst WHERE DATE BETWEEN '"+year+"-12-01' AND '"+year+"-12-31' ")	;
		ResultSet   rsdec=psdec.executeQuery();
		while(rsdec.next())
		{
			decStudent=rsdec.getString("NoofStudents");
		}
			
		
		noofstudents.add(janstudent);
		noofstudents.add(febstudent);
		noofstudents.add(marstudent);
		noofstudents.add(aprstudent);
		noofstudents.add(maystudent);
		noofstudents.add(junestudent);
		noofstudents.add(julystudent);
		noofstudents.add(augStudent);
		noofstudents.add(sepStudent);
		noofstudents.add(octStudent);
		noofstudents.add(novStudent);
		noofstudents.add(decStudent);
		
		}
		catch(Exception e)
		{
			System.out.println("Exception occur"+e);
		}
		
		return noofstudents;
		
	}
	
	
}
