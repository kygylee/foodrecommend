package com.android.recommedfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.recommedfood.databinding.ActivityMainBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private List<String> foodList = new ArrayList<>();
    private ArrayList<String> pickFoodList = new ArrayList<>();
    private ArrayList<String> resList = new ArrayList<>();
    private int curQuestion = 0;
    private boolean isFirst = false;
    private ProgressBar progressBar; // 추가한 ProgressBar 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        progressBar = mBinding.progressBar; // ProgressBar 초기화

        getFoodDataToText();

        //시작하기 버튼 클릭시 화면 visiable 전환
        mBinding.btStart.setOnClickListener(v -> {
            mBinding.layoutStart.setVisibility(View.GONE);
            mBinding.layoutQuestion.setVisibility(View.VISIBLE);
            setQuestion("나는 ____ 종류의 음식을 먹고 싶다.", "한식", "중식", "일식", "양식", "기타", null);        //1번
            curQuestion++;
        });

        mBinding.btNext.setOnClickListener(v -> {
            increaseProgress(); // ProgressBar 상태 증가
            switch (curQuestion) {
                case 1:
                    if (addCondition())
                        setQuestion("나는 지금 ____을 먹고 싶다.", "아침", "브런치","점심", "저녁", null,null);        //2번
                    else
                        Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (addCondition())
                        setQuestion("나는 ____이(가) 들어 갔으면 좋겠다.", "밥", "빵", "면", "고기", "채소", null);        //3번
                    else
                        Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (addCondition())
                        setQuestion("나는 ____요리를 먹고 싶다.", "볶음", "튀김", "구이", "찜(탕)", "날(것)", "기타");        //4번
                    else
                        Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (addCondition())
                        setQuestion("나는 ____좋아 한다.", "매움", "안매움", null, null, null, null);        //5번
                    else
                        Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    if (addCondition())
                        setQuestion("나는 ____음식을 먹고 싶다.", "차가움", "뜨거움", null, null, null, null);        //6번
                    else
                        Toast.makeText(this, "답변을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    addCondition();
                    filterFoodList();
                    mBinding.layoutStart.setVisibility(View.VISIBLE);
                    mBinding.layoutQuestion.setVisibility(View.GONE);
                    TextView textView1 = findViewById(R.id.tv_title1);
                    textView1.setVisibility(View.GONE);  // 안 보이게 하기

                    TextView textView2 = findViewById(R.id.tv_title);
                    textView2.setTextSize(20);  // 텍스트 크기를 20sp로 설정

                    String s = String.join(", ", resList);

                    if (resList.size() == 0)
                        mBinding.tvTitle.setText("오늘의 추천 메뉴는 없습니다.");
                    else
                        mBinding.tvTitle.setText("오늘의 추천 메뉴는 " + s + " 입니다.");

                    mBinding.btStart.setText("다시하기");
                    pickFoodList.clear();
                    resList.clear();
                    curQuestion = 0;
                    resetProgress(); // ProgressBar 상태 초기화
                    break;
            }
        });
    }

    private void resetProgress() { // ProgressBar 상태 초기화 함수
        progressBar.setProgress(0);
    }

    private void increaseProgress() { // ProgressBar 상태 증가 함수
        int progress = progressBar.getProgress();
        progressBar.setProgress(++progress);
    }

    private void filterFoodList() {
        for (int i = 0; i < foodList.size(); i++) {
            String foodName = foodList.get(i).substring(0, foodList.get(i).indexOf(":"));
            String condition = foodList.get(i).substring(foodList.get(i).indexOf(":") + 1);

            for (int j = 0; j < pickFoodList.size(); j++) {
                if (condition.contains(pickFoodList.get(j))) {
                    if (j == pickFoodList.size() - 1) {
                        resList.add(foodName);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private Boolean addCondition() {
        if (!(mBinding.radio1.isChecked() || mBinding.radio2.isChecked() || mBinding.radio3.isChecked() || mBinding.radio4.isChecked() || mBinding.radio5.isChecked() || mBinding.radio6.isChecked()))
            return false;


            if (mBinding.radio1.isChecked()) {
                pickFoodList.add(mBinding.radio1.getText().toString());
            }

            if (mBinding.radio2.isChecked()) {
                pickFoodList.add(mBinding.radio2.getText().toString());
            }

            if (mBinding.radio3.isChecked()) {
                pickFoodList.add(mBinding.radio3.getText().toString());
            }

            if (mBinding.radio4.isChecked()) {
                pickFoodList.add(mBinding.radio4.getText().toString());
            }

            if (mBinding.radio5.isChecked()) {
                pickFoodList.add(mBinding.radio5.getText().toString());
            }

            if (mBinding.radio6.isChecked()) {
                pickFoodList.add(mBinding.radio6.getText().toString());
            }

            curQuestion++;
            return true;
        }

        private void getFoodDataToText () {
            InputStream inputStream = getResources().openRawResource(R.raw.food_db);
            try {
                byte[] b = new byte[inputStream.available()];
                inputStream.read(b);
                String companyList = new String(b);
                String[] res = companyList.split("\r\n");

                foodList = Arrays.asList(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setQuestion (String question, String answer1, String answer2, String
        answer3, String answer4, String answer5, String answer6){
            {
                mBinding.radio1.setChecked(false);
                mBinding.radio2.setChecked(false);
                mBinding.radio3.setChecked(false);
                mBinding.radio4.setChecked(false);
                mBinding.radio5.setChecked(false);
                mBinding.radio6.setChecked(false);


                //1번
                mBinding.tvQuestion.setText(question);
                mBinding.radio1.setText(answer1);
                mBinding.radio2.setText(answer2);


                if (answer3 != null) {
                    mBinding.radio3.setVisibility(View.VISIBLE);
                    mBinding.radio3.setText(answer3);
                } else {
                    mBinding.radio3.setVisibility(View.GONE);
                }


                if (answer4 != null) {
                    mBinding.radio4.setVisibility(View.VISIBLE);
                    mBinding.radio4.setText(answer4);
                } else {
                    mBinding.radio4.setVisibility(View.GONE);
                }


                if (answer5 != null) {
                    mBinding.radio5.setVisibility(View.VISIBLE);
                    mBinding.radio5.setText(answer5);
                } else {
                    mBinding.radio5.setVisibility(View.GONE);
                }


                if (answer6 != null) {
                    mBinding.radio6.setVisibility(View.VISIBLE);
                    mBinding.radio6.setText(answer6);
                } else {
                    mBinding.radio6.setVisibility(View.GONE);
                }
            }
        }
    }