package my.app.projetdentaire.api;

import java.util.List;

import my.app.projetdentaire.beans.PW;
import my.app.projetdentaire.beans.Student;
import my.app.projetdentaire.beans.StudentPW;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StudentApi {

    @POST("login")
    Call<Student> login(@Body Student student);

    @GET("{id}")
    Call<Student>getStudentById(@Path("id") Long id);

    @PUT("{id}")
    Call <Void> updateStudent(@Path("id") Long id, @Body Student student);

    @PUT("image/{id}")
    Call<Void> updateStudentImage(@Path("id") Long id, @Part MultipartBody.Part image);

    @GET("pw/{id}")
    Call<List<PW>> getPW(@Path("id") Long id);

    @POST("add/{sid}/{pid}")
    Call<StudentPW> create(@Path("sid") Long studentId, @Path("pid") Long pwId, @Query("studentId") Long queryStudentId, @Query("pwId") Long queryPwId, @Body StudentPW studentPW);

}