package com.bible.titlegenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private LinearLayout resultsContainer;

    private final List<String> generatedTitles = new ArrayList<>();

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildInterface();
    }

    private void buildInterface() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setBackgroundColor(Color.rgb(248, 249, 250));

        // TITLE
        TextView title = new TextView(this);
        title.setText("Bible Title Generator");
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(25, 25, 25));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));

        root.addView(title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // DESCRIPTION
        TextView description = new TextView(this);
        description.setText(
                "Enter one Bible chapter per line.\n" +
                "Example: Genesis 1, Genesis 2, Genesis 3"
        );
        description.setTextSize(15);
        description.setTextColor(Color.DKGRAY);
        description.setPadding(dp(4), 0, dp(4), dp(10));

        root.addView(description);

        // INPUT
        inputText = new EditText(this);

        inputText.setHint(
                "Genesis 1\n" +
                "Genesis 2\n" +
                "Genesis 3"
        );

        inputText.setTextSize(17);
        inputText.setGravity(Gravity.TOP | Gravity.START);
        inputText.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        inputText.setSingleLine(false);
        inputText.setMinLines(7);
        inputText.setPadding(dp(14), dp(14), dp(14), dp(14));
        inputText.setBackgroundColor(Color.WHITE);

        root.addView(
                inputText,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(180)
                )
        );

        // BUTTON ROW
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, dp(12), 0, dp(12));

        Button generateButton = new Button(this);
        generateButton.setText("GENERATE TITLES");
        generateButton.setTextSize(14);
        generateButton.setAllCaps(false);

        Button clearButton = new Button(this);
        clearButton.setText("CLEAR");
        clearButton.setTextSize(14);
        clearButton.setAllCaps(false);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1
                );

        buttonParams.setMargins(0, 0, dp(6), 0);
        buttonRow.addView(generateButton, buttonParams);

        LinearLayout.LayoutParams clearParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        0.45f
                );

        clearParams.setMargins(dp(6), 0, 0, 0);
        buttonRow.addView(clearButton, clearParams);

        root.addView(buttonRow);

        // RESULTS HEADER
        LinearLayout resultHeader = new LinearLayout(this);
        resultHeader.setOrientation(LinearLayout.HORIZONTAL);
        resultHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView resultTitle = new TextView(this);
        resultTitle.setText("Generated Titles");
        resultTitle.setTextSize(19);
        resultTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        resultTitle.setTextColor(Color.rgb(30, 30, 30));

        resultHeader.addView(
                resultTitle,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        Button copyAllButton = new Button(this);
        copyAllButton.setText("COPY ALL");
        copyAllButton.setTextSize(13);
        copyAllButton.setAllCaps(false);

        resultHeader.addView(
                copyAllButton,
                new LinearLayout.LayoutParams(
                        dp(110),
                        dp(48)
                )
        );

        root.addView(resultHeader);

        // RESULTS SCROLL
        ScrollView resultScroll = new ScrollView(this);

        resultsContainer = new LinearLayout(this);
        resultsContainer.setOrientation(LinearLayout.VERTICAL);
        resultsContainer.setPadding(0, dp(8), 0, dp(20));

        resultScroll.addView(resultsContainer);

        root.addView(
                resultScroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        setContentView(root);

        generateButton.setOnClickListener(v -> generateTitles());

        clearButton.setOnClickListener(v -> {
            inputText.setText("");
            resultsContainer.removeAllViews();
            generatedTitles.clear();
        });

        copyAllButton.setOnClickListener(v -> copyAllTitles());
    }

    private void generateTitles() {

        resultsContainer.removeAllViews();
        generatedTitles.clear();

        String input = inputText.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(
                    this,
                    "Enter at least one Bible chapter.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String[] lines = input.split("\\r?\\n");

        int count = 0;

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String title = createTitle(line);

            if (title != null) {
                generatedTitles.add(title);
                addResultCard(title, generatedTitles.size());
                count++;
            }
        }

        if (count == 0) {
            Toast.makeText(
                    this,
                    "No valid Bible chapter lines found.",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    count + " title(s) generated.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String createTitle(String input) {

        input = input.trim();

        // Remove accidental colon/reference from the input.
        // Example:
        // Genesis 1:5 -> Genesis 1
        int colonPosition = input.indexOf(':');

        if (colonPosition >= 0) {
            input = input.substring(0, colonPosition).trim();
        }

        if (input.isEmpty()) {
            return null;
        }

        // Find final space separating book name and chapter.
        int lastSpace = input.lastIndexOf(' ');

        if (lastSpace <= 0 || lastSpace >= input.length() - 1) {
            return null;
        }

        String book = input.substring(0, lastSpace).trim();
        String chapter = input.substring(lastSpace + 1).trim();

        if (book.isEmpty() || chapter.isEmpty()) {
            return null;
        }

        // Keep only the numeric chapter portion.
        chapter = chapter.replaceAll("[^0-9]", "");

        if (chapter.isEmpty()) {
            return null;
        }

        String reference = book + " " + chapter + ":3";

        String hashtag = book
                .toLowerCase(Locale.US)
                .replaceAll("[^a-z0-9]", "");

        if (hashtag.isEmpty()) {
            hashtag = "bible";
        }

        return reference +
                " Bible Reading | Bible Verse Of The Day " +
                "#shorts #bibleverse #" +
                hashtag;
    }

    private void addResultCard(String title, int number) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, dp(6), 0, dp(6));

        resultsContainer.addView(card, cardParams);

        TextView numberText = new TextView(this);
        numberText.setText("TITLE " + number);
        numberText.setTextSize(12);
        numberText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        numberText.setTextColor(Color.rgb(90, 90, 90));

        card.addView(numberText);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(Color.rgb(25, 25, 25));
        titleText.setPadding(0, dp(8), 0, dp(10));

        card.addView(titleText);

        Button copyButton = new Button(this);
        copyButton.setText("COPY");
        copyButton.setAllCaps(false);

        copyButton.setOnClickListener(v -> {
            copyToClipboard(title);
            Toast.makeText(
                    this,
                    "Title copied.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        card.addView(
                copyButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(48)
                )
        );
    }

    private void copyToClipboard(String text) {

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE
                );

        ClipData clip =
                ClipData.newPlainText(
                        "Bible Title",
                        text
                );

        clipboard.setPrimaryClip(clip);
    }

    private void copyAllTitles() {

        if (generatedTitles.isEmpty()) {
            Toast.makeText(
                    this,
                    "Generate titles first.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        StringBuilder all = new StringBuilder();

        for (int i = 0; i < generatedTitles.size(); i++) {

            all.append(generatedTitles.get(i));

            if (i < generatedTitles.size() - 1) {
                all.append("\n");
            }
        }

        copyToClipboard(all.toString());

        Toast.makeText(
                this,
                "All titles copied.",
                Toast.LENGTH_SHORT
        ).show();
    }
}
