package ATM;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public interface WorkWithFile
{
    String fileName = "information.txt";
    String filefolder = "src/ATM";
    File file = new File(filefolder, fileName);

    private String prepareToWritingToFile(BankCard bankCard)
    {
        String cardNumber = bankCard.getCardNumber();
        return cardNumber.substring(0, 4) + "-" + cardNumber.substring(4, 8) + "-" +
                cardNumber.substring(8, 12) + "-" + cardNumber.substring(12, 16);
    }

    private ArrayList<BankCard> prepareAReadBanksCard(String str)
    {
        String[] data = str.split("\n");
        if (data != null)
        {
            ArrayList<BankCard> bankCards = new ArrayList<>();
            BankCard bankCard;
            String[] splitedInformation;
            for (String information : data)
            {
                splitedInformation = information.split(" ");
                bankCard = new BankCard(splitedInformation[0].replaceAll("-", ""), splitedInformation[1], Double.parseDouble(splitedInformation[2]));
                if (splitedInformation[3].equals("1"))
                {
                    bankCard.setTimeThenWasBanned(Instant.parse(splitedInformation[4]));
                    if (isTheRequiredTimePassSinceBan(bankCard.getTimeThenWasBanned()))
                    {
                        bankCard.setBaned(false);
                        bankCard.setTimeThenWasBanned(null);
                    }
                    else
                    {
                        bankCard.setBaned(true);
                    }
                }
                else
                {
                    bankCard.setBaned(false);
                }
                bankCards.add(bankCard);
            }
            return bankCards;
        }
        else
        {
            return null;
        }
    }

    private boolean isTheRequiredTimePassSinceBan(Instant oldTime)
    {
        Duration passedTime = Duration.between(oldTime,Instant.now());
        return passedTime.toHours() >= 24;
    }

    default void writeBankCardsToFile(ArrayList<BankCard> bankCards) throws IOException
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
        {
            for (BankCard bankCard : bankCards)
            {
                bankCard.setCardNumber(prepareToWritingToFile(bankCard));
                bw.write(bankCard.getCardNumber() + " ");
                bw.write(bankCard.getPIN() + " ");
                bw.write(bankCard.getBalance() + " ");
                bw.write((bankCard.getIsBaned() ? "1" : "0"));
                if (bankCard.getTimeThenWasBanned() != null)
                {
                    bw.write(" " + bankCard.getTimeThenWasBanned());
                }
                bw.write("\n");
            }
        }
    }

    default ArrayList<BankCard> readBankCardsFromFile() throws IOException
    {
        String str = "";
        try (BufferedReader bf = new BufferedReader(new FileReader(file)))
        {
            int c;
            while ((c = bf.read()) != -1)
            {
                str += (char) c;
            }
        } catch (IOException e)
        {
            return null;
        }
        return prepareAReadBanksCard(str);
    }
}
