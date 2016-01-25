package com.cl.interpolatordebugger;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    NormalRecyclerViewAdapter mNormalRecyclerViewAdapter;
    InterpolatorView mInterpolatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mInterpolatorView = (InterpolatorView) findViewById(R.id.interpolatorview);
        mNormalRecyclerViewAdapter = new NormalRecyclerViewAdapter(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNormalRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private String[] mTitles;
        private String[] mContents;

        public NormalRecyclerViewAdapter(Context context) {
            mTitles = context.getResources().getStringArray(R.array.interpolator_titles);
            mContents = context.getResources().getStringArray(R.array.interpolator_contents);
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_text, parent, false));
        }

        @Override
        public void onBindViewHolder(NormalTextViewHolder holder, int position) {
            holder.mTextViewTitle.setText(mTitles[position]);
            holder.mTextViewContent.setText(mContents[position]);

        }

        private TimeInterpolator getInterpolator(String className) {
            return getInterpolator("android.view.animation", className, Float.NaN);
        }
        private TimeInterpolator getInterpolator(String className,float parameter) {
            return getInterpolator("android.view.animation", className, parameter);
        }

        private TimeInterpolator getInterpolator(String packagename, String className, float parameter) {
            Class<?> interpolatorClass = null;
            try {
                interpolatorClass = Class.forName(packagename + "." + className);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Constructor<?> con[] = interpolatorClass.getConstructors();
                if(Float.isNaN(parameter)) {
                    return (TimeInterpolator) con[0].newInstance();
                }
                else {
                    return (TimeInterpolator) con[0].newInstance(parameter);
                }
//                return (TimeInterpolator) interpolatorClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return mTitles == null ? 0 : mTitles.length;
        }

        public class NormalTextViewHolder extends RecyclerView.ViewHolder {
            TextView mTextViewTitle;
            TextView mTextViewContent;

            NormalTextViewHolder(View view) {
                super(view);
                mTextViewTitle = (TextView) view.findViewById(R.id.text_title);
                mTextViewContent = (TextView) view.findViewById(R.id.text_content);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String interpolatorStr = ((TextView) v.findViewById(R.id.text_title)).getText().toString().replace(" ", "");
                        if (interpolatorStr.indexOf("(") != -1) {
                            String className = interpolatorStr.substring(0, interpolatorStr.indexOf("("));
                            float para = Float.valueOf(interpolatorStr.substring(interpolatorStr.indexOf("(") + 1, interpolatorStr.indexOf(")")));
                            TimeInterpolator timeInterpolator = getInterpolator(className, para);
                            mInterpolatorView.setInterpolator(timeInterpolator);
                        } else {
                            TimeInterpolator timeInterpolator = getInterpolator(interpolatorStr);
                            mInterpolatorView.setInterpolator(timeInterpolator);
                        }

                        mInterpolatorView.start();

                    }
                });
            }
        }
    }
}
