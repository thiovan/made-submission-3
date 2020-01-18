package thiovan.submission3.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import thiovan.submission3.R;
import thiovan.submission3.activities.DetailActivity;
import thiovan.submission3.adapters.TvShowListAdapter;
import thiovan.submission3.models.TvShowList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvShowFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String STATE_LIST = "state_list";

    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvTvShow;
    private ArrayList<TvShowList> listTvShow = new ArrayList<>();
    private String currentLanguage = "en";

    public TvShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tv_show, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        rvTvShow = rootView.findViewById(R.id.rv_tv_show);
        rvTvShow.setHasFixedSize(true);

        currentLanguage = Locale.getDefault().getLanguage();
        if (currentLanguage.equals("in")) {
            currentLanguage = "id";
        }

        if (savedInstanceState == null) {
            fetchTvShowList();
        } else {
            listTvShow = savedInstanceState.getParcelableArrayList(STATE_LIST);
            showRecyclerView();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, listTvShow);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!currentLanguage.equals(Locale.getDefault().getLanguage())) {
            currentLanguage = Locale.getDefault().getLanguage();
            if (currentLanguage.equals("in")) {
                currentLanguage = "id";
            }
            fetchTvShowList();
        }
    }

    @Override
    public void onRefresh() {
        fetchTvShowList();
    }

    private void showRecyclerView() {
        rvTvShow.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        TvShowListAdapter tvShowListAdapter = new TvShowListAdapter(listTvShow);
        rvTvShow.setAdapter(tvShowListAdapter);

        tvShowListAdapter.setOnItemClickCallback(new TvShowListAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(TvShowList data) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_ID, data.getId());
                intent.putExtra(DetailActivity.EXTRA_TYPE, "tv");
                startActivity(intent);
            }
        });
    }

    private void fetchTvShowList() {
        mSwipeRefreshLayout.setRefreshing(true);

        AndroidNetworking.get("https://api.themoviedb.org/3/discover/tv")
                .addQueryParameter("api_key", "a050df5725f01a6d3fe03f86baecd970")
                .addQueryParameter("language", currentLanguage)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONArray results = response.getJSONArray("results");

                            listTvShow = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentMovie = results.getJSONObject(i);

                                TvShowList tvShowList = new TvShowList();
                                tvShowList.setPhoto("https://image.tmdb.org/t/p/w185" + currentMovie.getString("poster_path"));
                                tvShowList.setName(currentMovie.getString("name"));
                                tvShowList.setDescription(currentMovie.getString("overview"));
                                tvShowList.setId(currentMovie.getString("id"));
                                listTvShow.add(tvShowList);

                                showRecyclerView();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(rootView.getContext(), getResources().getString(R.string.error_parse), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(rootView.getContext(), getResources().getString(R.string.error_fetch), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
