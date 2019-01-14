package pl.moviebook;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

import pl.moviebook.dbEntities.*;
import pl.moviebook.otherEntities.*;

import java.sql.Date;

@CrossOrigin
@Controller
@SpringBootApplication
public class BackendApplication {
	
	SessionFactory sessionFactory = Connection.getSessionFactory();

	private Date getDateRiGCZFormat(int year, int month, int day)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);

		return new Date(cal.getTime().getTime());
	}
	private Date getDateTimeRiGCZFormat(int year, int month, int day, int hour, int minute, int second)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);

		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);

		return new Date(cal.getTime().getTime());
	}

	@CrossOrigin
	@RequestMapping("/addIssue/{Movie_idMovie}/{User_login}/{dateYear}/{dateMonth}/{dateDay}/{timeHour}/{timeMinute}/{timeSecond}/{description}")
	@ResponseBody
	public String addIssue(@PathVariable("Movie_idMovie") int idMovie,
						@PathVariable("User_login") String User_login,
						@PathVariable("dateYear") int dateYear,
						@PathVariable("dateMonth") int dateMonth,
						@PathVariable("dateDay") int dateDay,
						@PathVariable("timeHour") int timeHour,
						@PathVariable("timeMinute") int timeMinute,
						@PathVariable("timeSecond") int timeSecond,
						@PathVariable("description") String description) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Issue issue = new Issue();
		issue.setIdMovie(idMovie);
		issue.setLogin(User_login);
		issue.setIssueDateTime(getDateTimeRiGCZFormat(dateYear, dateMonth, dateDay, timeHour, timeMinute, timeSecond));
		issue.setDescription(description);
		session.save(issue);
		try{
			session.getTransaction().commit();
		} catch(Exception e) {
			session.close();
			return "Unsuccessful<br />" + e.getMessage();
		}
		session.close();
		return "Successful";
		
		
	}

	@CrossOrigin
	@RequestMapping("/addToWatch/{Movie_idMovie}/{User_login}")
	@ResponseBody
	public String addToWatch(@PathVariable("Movie_idMovie") int idMovie,
						@PathVariable("User_login") String User_login) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		ToWatch towatch = new ToWatch();
		towatch.setIdMovie(idMovie);
		towatch.setLogin(User_login);
		session.save(towatch);
		try{
			session.getTransaction().commit();
		} catch(Exception e) {
			session.close();
			return "Unsuccessful";
		}
		session.close();
		return "Successful";
		
		
	}
	
	@CrossOrigin
	@RequestMapping("/removeToWatch/{Movie_idMovie}/{User_login}")
	@ResponseBody
	public String removeToWatch(@PathVariable("Movie_idMovie") int idMovie,
						@PathVariable("User_login") String User_login) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		ToWatch towatch = new ToWatch();
		towatch.setIdMovie(idMovie);
		towatch.setLogin(User_login);
		session.delete(towatch);
		try{
			session.getTransaction().commit();
		} catch(Exception e) {
			session.close();
			return "Unsuccessful";
		}
		session.close();
		return "Successful";
		
		
	}

	@CrossOrigin
	@RequestMapping("/addReview/{Movie_idMovie}/{User_login}/{content}")
	@ResponseBody
	public String addReview(@PathVariable("Movie_idMovie") int idMovie,
						@PathVariable("User_login") String User_login,
						@PathVariable("content") String content) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Review review = new Review();
		review.setIdMovie(idMovie);
		review.setLogin(User_login);
		review.setContent(content);
		session.save(review);
		try{
			session.getTransaction().commit();
		} catch(Exception e) {
			session.close();
			return "Unsuccessful";
		}
		session.close();
		return "Successful";
		
		
	}
	
	@RequestMapping("/allArtists")
	@ResponseBody
	public List<Artist> getAllArtists() {
		Session session = sessionFactory.openSession();

		// HQL syntax - createQuery
		// SQL syntax - createSQLQery
		Query<Artist> query = session.createQuery("from Artist");
		List<Artist> list = query.list();
		
		session.close();

		return list;
	}
	
	@CrossOrigin
	@RequestMapping("/login/{login}/{password}")
	@ResponseBody
	public String login(@PathVariable("login") String login,
						@PathVariable("password") String password) {
		
		Session session = sessionFactory.openSession();
		
		Query query = session.createSQLQuery(
			"SELECT UserType_name FROM User" + 
			"WHERE login = :login AND password = :password")
			.setParameter("login", login)
				.setParameter("password", password);
		
		String userTypeResult;
		
		try {
			userTypeResult = (String) query.getSingleResult();
		} catch (Exception e) {
			System.out.print(e);
			userTypeResult = null;
		}
		session.close();
		
		return userTypeResult;
	}

	@RequestMapping("/allCinemas")
	@ResponseBody
	public List<Artist> getAllCinemas() {
		Session session = sessionFactory.openSession();

		Query<Artist> query = session.createQuery("from Cinema");
		List<Artist> list = query.list();
		
		session.close();

		return list;
	}
	
	@RequestMapping("/allMovies")
	@ResponseBody
	public List<MovieBasicInformations> getAllMovies() {
		Session session = sessionFactory.openSession();
		
		Query<Movie> query = session.createQuery("from Movie");
		List<Movie> result = query.list();
		List<MovieBasicInformations> liteResult = new ArrayList<>();
		
		for(Movie movie : result) {
			
			List<String> genres = getMovieGenres(movie.getIdMovie(), session);
			
			MovieBasicInformations movieLite = new MovieBasicInformations(
				movie.getIdMovie(), movie.getTitle(), movie.getDateOfPremiere(), 
				movie.getPictureUrl(), genres);
			liteResult.add(movieLite);
		}
		
		session.close();
		return liteResult;
	}
	
	private List<String> getMovieGenres(int movieId, Session session) {
		Query querySQL = session.createSQLQuery(
			"SELECT Movie_has_Genre.Genre_name FROM Movie_has_Genre "
		  + "INNER JOIN Movie ON Movie_has_Genre.Movie_idMovie = Movie.idMovie WHERE Movie.idMovie = :id")
				.setParameter("id", movieId);
		
		return querySQL.list();
	}

	@RequestMapping("/movie/{idMovie}")
	@ResponseBody
	public MovieFullInformations getMovie(@PathVariable("idMovie") int idMovie) {
		Session session = sessionFactory.openSession();

		Movie movie;

		try {
            movie =  (Movie) session.get(Movie.class, idMovie);
        } catch (Exception e) {
			System.out.print(e);
			session.close();
			return null;
		}
		List<String> genres = getMovieGenres(movie.getIdMovie(), session);
		
		Query querySQL = session.createSQLQuery("SELECT artist.idArtist, artist.name, artist.surname, artist.pictureUrl, a.Role, type.ArtistType_name FROM Artist as artist " + 
				"INNER JOIN Artist_has_ArtistType as type ON type.Artist_idArtist = artist.idArtist " + 
				"INNER JOIN Movie_has_Artist as a ON type.id = a.Artist_has_ArtistType_id " + 
				"INNER JOIN Movie as movie ON movie.idMovie = a.Movie_idMovie " + 
				"WHERE movie.idMovie = :id")
				.setParameter("id", movie.getIdMovie());
		
		List<Object[]> artistsSQLResult = (List<Object[]>) querySQL.list();
		List<ArtistInFilmBasicInformations> artists = new ArrayList<>();
		
		for( Object[] artist : artistsSQLResult) {
			ArtistInFilmBasicInformations data = new ArtistInFilmBasicInformations((int) artist[0], (String) artist[1],
					(String) artist[2],(String) artist[3],(String) artist[5], (String) artist[4]);
			artists.add(data);
		}
		
		querySQL = session.createSQLQuery("SELECT Review.idReview, Review.content FROM Review " + 
				"INNER JOIN Movie ON Review.Movie_idMovie = Movie.idMovie " + 
				"WHERE Movie.idMovie = :id")
				.setParameter("id", movie.getIdMovie());
		
		List<Object[]> reviewsSQLResult = (List<Object[]>) querySQL.list();
		List<ReviewWithLikes> reviews = new ArrayList<>();
		
		for(Object[] review : reviewsSQLResult) {
			
			querySQL = session.createSQLQuery("SELECT COUNT(Review_idReview) FROM `Like` WHERE `Like`.Review_idReview = :id")
					.setParameter("id", (int) review[0]);
			int amountOfLikes = ((BigInteger) querySQL.getSingleResult()).intValue();
			
			reviews.add(new ReviewWithLikes((int) review[0], (String) review[1], amountOfLikes));
		}
		
		querySQL = session.createQuery("FROM Prize WHERE Movie_idMovie = :id").setParameter("id", movie.getIdMovie());
		List<Prize> prizes = (List<Prize>) querySQL.list();
		
		List<ShowWithCinema> showsWithCinema = new ArrayList<>();
		querySQL = session.createSQLQuery("SELECT `Show`.dateTime, Cinema.name, Cinema.city FROM `Show` "
				+ "INNER JOIN Cinema ON `Show`.Cinema_idCinema = Cinema.idCinema WHERE `Show`.Movie_idMovie = :id")
				.setParameter("id", movie.getIdMovie());
		List<Object[]> showsSQLResult = (List<Object[]>) querySQL.list();
		
		for(Object[] show : showsSQLResult) {
			showsWithCinema.add(new ShowWithCinema(((Timestamp) show[0]), (String) show[1], (String) show[2]));
		}
		
		List<TvProgramBasicInformations> transmitions = new ArrayList<>();
		querySQL = session.createSQLQuery("SELECT TvProgram.station, TvProgram.dateTime FROM TvProgram WHERE Movie_idMovie = :id")
				.setParameter("id", movie.getIdMovie());
		
		List<Object[]> transmitionsSQLResult = querySQL.list();
		
		for(Object[] transmition : transmitionsSQLResult) {
			transmitions.add(new TvProgramBasicInformations((String) transmition[0], (Timestamp) transmition[1]));
		}
		
		
		BigDecimal rating = (BigDecimal) session.createSQLQuery("SELECT AVG(rate) FROM Rating WHERE Movie_idMovie = :id")
				.setParameter("id", movie.getIdMovie())
				.getSingleResult();
		
		MovieFullInformations movieFull = new MovieFullInformations(movie.getIdMovie(), movie.getTitle(),
				movie.getLanguage(), movie.getDateOfPremiere(), movie.getBoxOffice(), movie.getCountry(), 
				movie.getDescription(), movie.getPictureUrl(), artists, reviews, prizes, showsWithCinema, transmitions, rating, genres);  
		

		session.close();

		return movieFull;
	}
	
	@CrossOrigin
	@RequestMapping("/register/{login}/{password}")
	@ResponseBody
	public String register(@PathVariable("login") String login,
						@PathVariable("password") String password) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		User user = new User();
		user.setLogin(login);
		user.setPassword(password);
		user.setUserType("User");
		session.save(user);
		try{
			session.getTransaction().commit();
		} catch(Exception e) {
			session.close();
			return "Unsuccessful";
		}
		session.close();
		
		return "Successful";
	}

	@CrossOrigin
	@RequestMapping("/changeRating/{User_login}/{Movie_idMovie}/{rate}")
	@ResponseBody
	public String changeRating(
		@PathVariable("User_login") String User_login, 
		@PathVariable("Movie_idMovie") int Movie_idMovie,
		@PathVariable("rate") int rate) {
		
		Session session = sessionFactory.openSession();

		session.beginTransaction();

		Rating rating = new Rating();
		rating.setUser_login(User_login);
		rating.setMovie_idMovie(Movie_idMovie);
		rating.setRate(rate);

		session.saveOrUpdate(rating);
		try{
			session.getTransaction().commit();
		} catch(Exception e) {
			session.close();
			return "Unsuccessful";
		}
		
		session.close();
		
		return "Successful";
	}

	@CrossOrigin
	@RequestMapping("/allMoviesOfTheGenre/{name}")
	@ResponseBody
	public List<MovieBasicInformations> getAllMoviesOfTheGenre(
		@PathVariable("name") String name) {
		
		Session session = sessionFactory.openSession();
	
		Query query = session.createQuery(
			"select movie " + 
			"from Movie as movie, Movie_has_Genre as movie_has_Genre " + 
			"where movie.idMovie = movie_has_Genre.Movie_idMovie " + 
			"and movie_has_Genre.Genre_name = :name"
		).setParameter("name", name);

		List<Movie> movies = query.list();

		List<MovieBasicInformations> basicMovies = new ArrayList<>();
		for(Movie movie : movies) {
			
			List<String> genres = getMovieGenres(movie.getIdMovie(), session);
			
			MovieBasicInformations movieLite = new MovieBasicInformations(
				movie.getIdMovie(), movie.getTitle(), movie.getDateOfPremiere(), 
				movie.getPictureUrl(), genres);
			basicMovies.add(movieLite);
		}
		
		session.close();
		return basicMovies;
	}

	public static void main(String[] args) {
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
