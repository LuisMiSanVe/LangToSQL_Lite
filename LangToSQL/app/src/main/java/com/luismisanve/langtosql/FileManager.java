package com.luismisanve.langtosql;

import android.content.Context;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import android.security.keystore.*;
import android.widget.Toast;

public class FileManager {

    private static final String KEY_ALIAS = "LangToSqlKey";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final Context context;

    public FileManager(Context context) {
        this.context = context;
        createKeyIfNeeded();
    }

    private void createKeyIfNeeded() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {

                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        "AndroidKeyStore"
                );

                KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build();

                keyGenerator.init(keySpec);
                keyGenerator.generateKey();
            }

        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.error_file_internal) + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        return ((SecretKey) keyStore.getKey(KEY_ALIAS, null));
    }

    public void writeToFile(String fileName, String data) {
        File file = new File(context.getFilesDir(), fileName);

        if (file.exists()) {
            try {

                SecretKey secretKey = getSecretKey();

                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                byte[] iv = cipher.getIV();

                byte[] encryptedData = cipher.doFinal(
                        data.getBytes(StandardCharsets.UTF_8)
                );

                try (FileOutputStream fos =
                             context.openFileOutput(fileName, Context.MODE_PRIVATE)) {

                    // Save IV length
                    fos.write(iv.length);

                    // Save IV
                    fos.write(iv);

                    // Save encrypted content
                    fos.write(encryptedData);

                    fos.flush();
                }

            } catch (Exception e) {
                Toast.makeText(context, context.getString(R.string.error_file_internal) + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String readFromFile(String fileName) {
        File file = new File(context.getFilesDir(), fileName);

        if (file.exists()) {
            try {

                SecretKey secretKey = getSecretKey();

                try (FileInputStream fis = context.openFileInput(fileName)) {

                    // Read IV length
                    int ivLength = fis.read();

                    // Read IV
                    byte[] iv = new byte[ivLength];
                    fis.read(iv);

                    // Read encrypted bytes
                    byte[] encryptedData = new byte[fis.available()];
                    fis.read(encryptedData);

                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);

                    GCMParameterSpec spec =
                            new GCMParameterSpec(128, iv);

                    cipher.init(
                            Cipher.DECRYPT_MODE,
                            secretKey,
                            spec
                    );

                    byte[] decryptedData =
                            cipher.doFinal(encryptedData);

                    return new String(
                            decryptedData,
                            StandardCharsets.UTF_8
                    );
                }

            } catch (Exception e) {
                Toast.makeText(context, context.getString(R.string.error_file_internal) + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return "";
    }
}