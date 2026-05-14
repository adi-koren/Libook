package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.Uid;
import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.username;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.libookproject.libookapp.PublishPostRequest;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.CommunityApiService;

public class AddPostFragment extends Fragment
{
    private View view;
    private EditText eTHeadline;
    private EditText eTContent;
    private Button btnPublish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_post, container, false);

        init();

        return view;
    }

    private void init()
    {
        eTHeadline = view.findViewById(R.id.eTHeadline);
        eTContent = view.findViewById(R.id.eTContent);
        btnPublish = view.findViewById(R.id.btnPublish);

        btnPublish.setOnClickListener(v -> { publishClicked(); });
    }

    private void publishClicked()
    {
        String headline = eTHeadline.getText().toString();
        String content = eTContent.getText().toString();

        if (headline.length() == 0)
        {
            eTHeadline.setError("Field must be filled");
            return;
        }
        if (content.length() == 0)
        {
            eTContent.setError("Field must be filled");
            return;
        }

        PublishPostRequest publishPostRequest = new PublishPostRequest(Uid,
                username, headline, content);

        CommunityApiService.publishPost(publishPostRequest, new ApiCallback() {
            @Override
            public void onPublishPostSucceeded(int postId)
            {
                eTHeadline.setText("");
                eTContent.setText("");
                Toast.makeText(getContext(), "Your post has been published", Toast.LENGTH_SHORT).show();
                ((MainActivity)requireActivity()).notifyPostsHaveChanged();
            }

            @Override
            public void onPublishPostFailed(String err)
            {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

}