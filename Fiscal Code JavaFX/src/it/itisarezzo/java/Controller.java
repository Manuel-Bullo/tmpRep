package it.itisarezzo.java;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/*
*   Fiscal Code calculator
*   Created by Manuel Bulletti
*   Version 1.0
 */

public class Controller {
    @FXML private Button closeBtn;
    @FXML private ComboBox<String> provinceComboBox;
    @FXML private ComboBox<String> municipalityComboBox;
    @FXML private ChoiceBox<String> genderChoiceBox;
    @FXML private TextField fiscalCodeTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField dayTextField;
    @FXML private TextField monthTextField;
    @FXML private TextField yearTextField;
    @FXML private Label lastNameLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label municipalityLabel;
    @FXML private Label genderLabel;
    @FXML private Label dateLabel;

    private static final Clipboard clipboard = Clipboard.getSystemClipboard();
    private static final ClipboardContent content = new ClipboardContent();

    private ComboBox<String> srcCB = new ComboBox<String>();
    private TextField srcTF = new TextField("");
    private ArrayList<TextField> emptyTextFields = new ArrayList<TextField>();
    private ArrayList<TextField> textFields = new ArrayList<TextField>();
    private ArrayList<Label> labels = new ArrayList<Label>();
    private final String municipalityValidValues = "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÌÒÙ ";
    private final String namesValidValues = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String[] keysToIgnore = {"LEFT", "RIGHT", "BACK_SPACE", "ENTER", "TAB", "SPACE"};
    private String oldTextDay = "", oldTextMonth = "", oldTextYear = "";
    private boolean isMunCBValueCorrect = true;

    void textFieldControl(TextField src, Label srcLabel) {
        if(src.getText().equals("")) {
            emptyTextFields.add(src);
            srcLabel.setStyle("-fx-text-fill: red;");
        } else {
            emptyTextFields.remove(src);
            srcLabel.setStyle("-fx-text-fill: #007918;");
        }
    }

    @FXML
    void calcBtnPressed(ActionEvent event) {
        boolean areAllFieldsCompiled = true;

        for(int i = 0; i < textFields.size(); i++)
            textFieldControl(textFields.get(i), labels.get(i));

        if(dayTextField.getText().equals("") || monthTextField.getText().equals("") || yearTextField.getText().equals("")) {
            dateLabel.setStyle("-fx-text-fill: red;");
            areAllFieldsCompiled = false;
        } else
            dateLabel.setStyle("-fx-text-fill: #007918;");

        if(municipalityComboBox.getEditor().getText().equals("") || !isMunCBValueCorrect) {
            areAllFieldsCompiled = false;
            municipalityLabel.setStyle("-fx-text-fill: red;");
        } else {
            municipalityLabel.setStyle("-fx-text-fill: #007918;");
            provinceComboBox.getEditor().setStyle("-fx-text-fill: black;");
        }

        if(genderChoiceBox.getValue() == null) {
            areAllFieldsCompiled = false;
            genderLabel.setStyle("-fx-text-fill: red;");
        } else
            genderLabel.setStyle("-fx-text-fill: #007918;");

        if(!emptyTextFields.isEmpty())
            areAllFieldsCompiled = false;

        if(!areAllFieldsCompiled) {
            fiscalCodeTextField.setStyle("-fx-text-fill: red;");
            return;
        } else
            fiscalCodeTextField.setStyle("-fx-text-fill: black;");

        String fiscalCode = "";
        boolean isMale;

        if(!genderChoiceBox.getValue().equals("M"))
            isMale = false;
        else
            isMale = true;

        fiscalCode += fiscalCodeSurname(lastNameTextField.getText());

        fiscalCode += fiscalCodeName(firstNameTextField.getText());

        fiscalCode += fiscalCodeYear(Integer.parseInt(yearTextField.getText()));

        fiscalCode += fiscalCodeMonth(Integer.parseInt(monthTextField.getText()));

        fiscalCode += fiscalCodeDay(Integer.parseInt(dayTextField.getText()), isMale);

        fiscalCode += municipalityCode(municipalityComboBox.getEditor().getText());

        fiscalCode += controlInternalNumberCode(fiscalCode);

        fiscalCodeTextField.setText(fiscalCode);
    }

    @FXML
    void closeBtnPressed(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();

        stage.close();
    }

    /*
     *   Function that returns fiscal
     *   code's letter basing on a
     *   surname input
     */

    String fiscalCodeSurname(String surname) {
        String fiscalCodeBit = "";
        char[] charArray = surname.toCharArray();
        int counter = 0, consonantQuantity = 0;

        for(int i = 0; i < surname.length(); i++)
            if(consonantCheck(charArray[i]) /*&& charArray[i] != ' '*/)
                consonantQuantity++;

        if(consonantQuantity < 3) {
            for(int i = 0; i < surname.length(); i++)
                if (consonantCheck(charArray[i]))
                    fiscalCodeBit += charArray[i];

            int j = 0;
            while(fiscalCodeBit.length() < 3) {
                if(!consonantCheck(charArray[j]) /*&& charArray[j] != ' '*/)
                    fiscalCodeBit += charArray[j];

                j++;

                if(surname.length() == j)
                    break;
            }

            while(fiscalCodeBit.length() < 3) {
                fiscalCodeBit += 'X';
            }
        } else
            for(int i = 0; i < surname.length(); i++) {
                if(consonantCheck(charArray[i]) /*&& charArray[i] != ' '*/) {
                    if(counter >= 3)
                        break;
                    else
                        fiscalCodeBit += charArray[i];

                    counter++;
                }
            }

        return fiscalCodeBit;
    } // End fiscalCodeSurname

    /*
     *   Function that returns fiscal
     *   code's letter basing on a
     *   name input
     */

    String fiscalCodeName(String name) {
        String fiscalCodeBit = "";
        char[] charArray = name.toCharArray();
        int counter = 0, consonantQuantity = 0;

        for(int i = 0; i < name.length(); i++)
            if(consonantCheck(charArray[i]) /*&& charArray[i] != ' '*/)
                consonantQuantity++;

        if(consonantQuantity < 3) {
            for(int i = 0; i < name.length(); i++)
                if (consonantCheck(charArray[i]))
                    fiscalCodeBit += charArray[i];

            int j = 0;
            while(fiscalCodeBit.length() < 3) {
                if(!consonantCheck(charArray[j]) /*&& charArray[j] != ' '*/)
                    fiscalCodeBit += charArray[j];

                j++;

                if(name.length() == j)
                    break;
            }

            while(fiscalCodeBit.length() < 3)
                fiscalCodeBit += 'X';

        } else if(consonantQuantity < 4) {
            for(int i = 0; i < name.length(); i++) {
                if(consonantCheck(charArray[i]) /*&& charArray[i] != ' '*/) {
                    if(counter >= 3)
                        break;
                    else
                        fiscalCodeBit += charArray[i];

                    counter++;
                }
            }
        } else
            for(int i = 0; i < name.length(); i++) {
                if(consonantCheck(charArray[i]) /*&& charArray[i] != ' '*/) {
                    if(counter >= 4)
                        break;
                    else
                    if(counter != 1)
                        fiscalCodeBit += charArray[i];

                    counter++;
                }
            }

        return fiscalCodeBit;
    } // End fiscalCodeName

    /*
     *   Function that returns the last two
     *   character of the year of birth
     */

    String fiscalCodeYear(int year) {
        String fiscalCodeBit = "";
        String stringYear = Integer.toString(year);

        fiscalCodeBit += stringYear.charAt(stringYear.length() - 2);
        fiscalCodeBit += stringYear.charAt(stringYear.length() - 1);

        return fiscalCodeBit;
    } // End fiscalCodeYear

    /*
     *   Function that returns the letter
     *   of the corresponding month
     */

    String fiscalCodeMonth(int month) {
        String letters = "ABCDEHLMPRST";

        return Character.toString(letters.charAt(month - 1));
    } // End fiscalCodeMonth

    /*
     *   Function that returns the
     *   two characters of the day
     *   of birth plus 40 if the
     *   user is a female
     */

    String fiscalCodeDay(int dayOfBirth, boolean isMale) {
        if(!isMale)
            dayOfBirth += 40;
        else if(dayOfBirth < 10)
            return "0" + Integer.toString(dayOfBirth);

        return String.valueOf(dayOfBirth);
    } // End fiscalCodeDay

    /*
     *   Function that returns the four
     *   characters of the municipality
     *   code basing on the municipality
     *   received form input by the user
     */

    String municipalityCode(String municipality) {
        String fiscalCodeBit = "";

        try(Scanner fileData = new Scanner(Paths.get("data/listacomuni.txt"))) {
            fileData.nextLine();
            while(fileData.hasNext()) {
                String lineOfData = fileData.nextLine();
                String[] lineParts = lineOfData.split(";");

                if(lineParts[1].equalsIgnoreCase(municipality)) {
                    fiscalCodeBit = lineParts[6];
                    provinceComboBox.setValue(lineParts[2]);
                    break;
                }
            }
        } catch(IOException | NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

        return fiscalCodeBit;
    } // End municipalityCode

    /*
     *   Function that returns the control code
     *   of the fiscal code basing on the 15
     *   character string of the fiscal code
     */

    char controlInternalNumberCode(String fiscalCode) {
        char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        char[] characterArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        int[] evenCharacterValues = {0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
        int[] oddCharacterValues = {1,0,5,7,9,13,15,17,19,21,1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23};
        int result = 0;

        for(int i = 0; i < fiscalCode.length(); i++)
            if ((i + 1) % 2 == 0) {
                for (int j = 0; j < characterArray.length; j++)
                    if (characterArray[j] == fiscalCode.charAt(i)) {
                        result += evenCharacterValues[j];
                        break;
                    }
            } else
                for(int k = 0; k < characterArray.length; k++)
                    if(characterArray[k] == fiscalCode.charAt(i)) {
                        result += oddCharacterValues[k];
                        break;
                    }

        result %= 26;

        return alphabet[result];
    } // End checkCharacterCode

    /*
     *   This function check if
     *   a char is a consonant or not
     */

    boolean consonantCheck(char letter) {
        String vocals = "AEIOU";
        boolean isConsonant = false;

        if(!vocals.contains(Character.toString(letter)))
            isConsonant = true;

        return isConsonant;
    } // End consonantCheck

    @FXML
    void comboBoxInputHandler(KeyEvent event) {
        boolean doIgnoreKey = false;

        for(String entry : keysToIgnore) {
            if(entry.equals(event.getCode().toString()))
                doIgnoreKey = true;
        }

        if(event.getSource() == municipalityComboBox)
            srcCB = municipalityComboBox;
        else if(!doIgnoreKey) { //(event.getSource() == provinceComboBox)
            srcCB = provinceComboBox;
            srcCB.getEditor().setText(srcCB.getEditor().getText().toUpperCase());
            srcCB.getEditor().end();
        }

        //srcCB.setStyle("-fx-text-fill: black;");

        if(!municipalityValidValues.contains(event.getText().toUpperCase()) && !doIgnoreKey) { //!event.getCode().toString().equals("ENTER")
            srcCB.getEditor().deletePreviousChar();
            return;
        }

        if(!srcCB.getItems().contains(srcCB.getEditor().getText())) {
            srcCB.getEditor().setStyle("-fx-text-fill: red;");
            if(srcCB == municipalityComboBox)
                isMunCBValueCorrect = false;
        } else {
            srcCB.getEditor().setStyle("-fx-text-fill: black;");
            if(srcCB == municipalityComboBox)
                isMunCBValueCorrect = true;
        }
    }

    @FXML
    void textFieldInputHandler(KeyEvent event) {
        boolean doIgnoreKey = false;

        for(String entry : keysToIgnore) {
            if(entry.equals(event.getCode().toString()))
                doIgnoreKey = true;
        }

        if (event.getSource() == lastNameTextField)
            srcTF = lastNameTextField;
        else //(event.getSource() == firstNameTextField)
            srcTF = firstNameTextField;

        //srcTF.setStyle("-fx-text-fill: black;");

        if(!namesValidValues.contains(event.getText().toUpperCase()) && !doIgnoreKey)
            srcTF.deletePreviousChar();

        if(!doIgnoreKey) {
            srcTF.setText(srcTF.getText().toUpperCase());
            srcTF.end();
        }
    }

    @FXML
    void dayInputHandler(KeyEvent event) {
        int highestDay = 31;
        int month = 12, year = 1;

        try {
            month = Integer.parseInt(monthTextField.getText());
            year = Integer.parseInt(yearTextField.getText());
        } catch(Exception e) {

        }

        if(!(year % 4 == 0) && month == 2)
            month = 0;

        switch(month) {
            case 0:
                highestDay = 28;
                break;
            case 2:
                highestDay = 29;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                highestDay = 30;
                break;
            default:
                highestDay = 31;
                break;
        }

        //System.out.printf("\nhighestDay: %d\nmonth: %d\nyear: %d\n", highestDay, month, year); // Debug

        if((monthTextField == event.getSource() || yearTextField == event.getSource()) && !dayTextField.getText().equals(""))
            if(Integer.parseInt(dayTextField.getText()) > highestDay) {
                dayTextField.setText(Integer.toString(highestDay));
                return;
            }

        dateInputHandler(event, 1, highestDay);
    }

    @FXML
    void monthInputHandler(KeyEvent event) {
        dateInputHandler(event, 1, 12);
        dayInputHandler(event);
    }

    @FXML
    void yearInputHandler(KeyEvent event) {
        dateInputHandler(event, 1, 9999);
        dayInputHandler(event);
    }

    @FXML
    void dateInputHandler(KeyEvent event, int lowest, int highest) {
        TextField srcEvent = (TextField) event.getSource();
        String oldText = "";
        int textFieldValue = 0;

        srcEvent.setStyle("-fx-text-fill: black;");

        if(srcEvent == dayTextField)
            oldText = oldTextDay;
        else if(srcEvent == monthTextField)
            oldText = oldTextMonth;
        else
            oldText = oldTextYear;

        try {
            textFieldValue = Integer.parseInt(srcEvent.getText());
        } catch (Exception e) {
            srcEvent.deletePreviousChar();
            return;
        }

        if(textFieldValue == 0)
            oldText = "";

        if (textFieldValue < lowest || textFieldValue > highest) {
            srcEvent.setText(oldText);
            srcEvent.end();
        } else
            oldText = srcEvent.getText();

        if(srcEvent == dayTextField)
            oldTextDay = oldText;
        else if(srcEvent == monthTextField)
            oldTextMonth = oldText;
        else
            oldTextYear = oldText;
    }

    @FXML
    void onMouseClicked(MouseEvent event) {
        saveToClipboard(fiscalCodeTextField.getText());
    }

    private void saveToClipboard(String text){
        content.putString(text);
        clipboard.setContent(content);
    }

    @FXML
    void initialize() {
        Platform.runLater(()->lastNameTextField.requestFocus());

        // Add elements to a list to check empty text fields
        textFields.add(lastNameTextField);
        labels.add(lastNameLabel);
        textFields.add(firstNameTextField);
        labels.add(firstNameLabel);

        // Charge genderChoiceBox
        genderChoiceBox.getItems().addAll("M", "F");

        // Charge municipalityComboBox
        try(Scanner fileData = new Scanner(Paths.get("data/listacomuni.txt"))) {
            fileData.nextLine();
            while(fileData.hasNext()) {
                String lineOfData = fileData.nextLine();
                String[] lineParts = lineOfData.split(";");

                municipalityComboBox.getItems().add(lineParts[1]);
                provinceComboBox.getItems().add(lineParts[2]);
            }
        } catch(IOException | NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }
    }
}