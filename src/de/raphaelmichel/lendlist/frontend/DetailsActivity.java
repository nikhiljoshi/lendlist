package de.raphaelmichel.lendlist.frontend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;

public class DetailsActivity extends SherlockFragmentActivity {

	private Item item;

	private EditText etThing;
	private EditText etDate;
	private EditText etUntil;
	private Spinner spDirection;
	private Button btReturned;
	private QuickContactBadge qcbPerson;
	private TextView tvPerson;
	private ImageButton ibPersonEdit;

	private DialogFragment dpDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		DataSource data = new DataSource(this);
		data.open();
		item = data.getItem(getIntent().getLongExtra("id", 0));
		data.close();

		if (item == null) {
			setContentView(R.layout.error);
			Typeface handwrittenFace = Typeface.createFromAsset(getAssets(),
					"fonts/belligerent.ttf");
			((TextView) findViewById(R.id.tvHead)).setTypeface(handwrittenFace);
			((TextView) findViewById(R.id.tvBody)).setTypeface(handwrittenFace);
			((TextView) findViewById(R.id.tvBody))
					.setText(R.string.error_unkown_item);
		} else {
			setContentView(R.layout.activity_details);
			etThing = (EditText) findViewById(R.id.etThing);
			etDate = (EditText) findViewById(R.id.etDate);
			etUntil = (EditText) findViewById(R.id.etUntil);
			spDirection = (Spinner) findViewById(R.id.spDirection);
			btReturned = (Button) findViewById(R.id.btReturned);
			qcbPerson = (QuickContactBadge) findViewById(R.id.qcbPerson);
			tvPerson = (TextView) findViewById(R.id.tvPerson);
			ibPersonEdit = (ImageButton) findViewById(R.id.ibPersonEdit);

			etThing.setText(item.getThing());
			if (item.getDate() != null)
				etDate.setText(new SimpleDateFormat(
						getString(R.string.date_format)).format(item.getDate()));
			if (item.getUntil() != null)
				etUntil.setText(new SimpleDateFormat(
						getString(R.string.date_format)).format(item.getUntil()));
			tvPerson.setText(item.getPerson());

			// Date picker
			etUntil = (EditText) findViewById(R.id.etUntil);
			etUntil.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						dpDialog = new DatePickerFragmentUntil();
						dpDialog.show(getSupportFragmentManager(), "datePicker");
					}
					return true;
				}
			});

			etUntil.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						if (dpDialog == null) {
							dpDialog = new DatePickerFragmentUntil();
							dpDialog.show(getSupportFragmentManager(),
									"datePicker");
						}
					} else {
						if (dpDialog != null) {
							if (dpDialog.isVisible())
								dpDialog.dismiss();
							dpDialog = null;
						}
					}
				}
			});
			etDate = (EditText) findViewById(R.id.etDate);
			etDate.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						dpDialog = new DatePickerFragment();
						dpDialog.show(getSupportFragmentManager(), "datePicker");
					}
					return true;
				}
			});

			etDate.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						if (dpDialog == null) {
							dpDialog = new DatePickerFragment();
							dpDialog.show(getSupportFragmentManager(),
									"datePicker");
						}
					} else {
						if (dpDialog != null) {
							if (dpDialog.isVisible())
								dpDialog.dismiss();
							dpDialog = null;
						}
					}
				}
			});

			List<String> list = new ArrayList<String>();
			list.add(getString(R.string.edit_text_borrowed));
			list.add(getString(R.string.edit_text_lent));
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, list);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spDirection.setAdapter(dataAdapter);

			if (item.getDirection().equals("lent"))
				spDirection.setSelection(1);
			else
				spDirection.setSelection(0);

		}
	}

	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			if (etUntil.getText().length() > 0) {
				Date d;
				try {
					d = getDate();
					c.setTime(d);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@SuppressWarnings("deprecation")
		public void onDateSet(DatePicker view, int year, int month, int day) {
			etDate.setText(new SimpleDateFormat(getString(R.string.date_format))
					.format(new Date(year - 1900, month, day)));
		}

		protected Date getDate() throws ParseException {
			return new SimpleDateFormat(getString(R.string.date_format))
					.parse(etDate.getText().toString());
		}
	}

	public class DatePickerFragmentUntil extends DatePickerFragment {
		@SuppressWarnings("deprecation")
		public void onDateSet(DatePicker view, int year, int month, int day) {
			etUntil.setText(new SimpleDateFormat(
					getString(R.string.date_format)).format(new Date(
					year - 1900, month, day)));
		}

		protected Date getDate() throws ParseException {
			return new SimpleDateFormat(getString(R.string.date_format))
					.parse(etUntil.getText().toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}

	public void save() {
		Log.i("selected", "" + spDirection.getSelectedItem());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_accept:
			save();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}