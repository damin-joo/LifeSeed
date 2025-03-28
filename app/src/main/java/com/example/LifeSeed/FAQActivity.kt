package com.example.LifeSeed

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityFaqBinding

class FAQActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up back button
        binding.backButton.setOnClickListener {
            finish() // Close FAQActivity and go back to HomeActivity
        }

        // Questions and answers
        val questions = listOf(
            "1. Can I mix my hCG (human chorionic gonadotropin) trigger shot in advance and how long can it be left out of the refrigerator once it's mixed?",
            "2. What should I expect at my monitoring appointments?",
            "3. Why is birth control sometimes used at the beginning of a cycle?",
            "4. What is OHSS (Ovarian Hyperstimulation Syndrome) and what should I be looking for?",
            "5. Does Omnitrope have a generic alternative available?",
            "6. What happens to follicles after the trigger shot?",
            "7. What is the difference between low dose hCG vs. Menopur? Which one is better?",
            "8. How long should I try to conceive before seeing a doctor?",
            "9. How can I tell when menstrual cycle day 1 is?",
            "10. Is spotting before (not between) a period normal?",
            "11. What are some questions to ask at my initial consultation that can improve my experience at my fertility clinic?",
            "12. What is PRP (Platelet-Rich Plasma Therapy) and when is it used?",
            "13. What is the difference between a follicle and an egg?",
            "14. Should I lie down when I inject myself?",
            "15. What is a trigger shot in IVF and egg freezing?"
        )

        val answers = listOf(
            "You can premix your hCG trigger shot. Once the powder is reconstituted using the provided liquid, it can safely be refrigerated for at least 30 days and certain brands (like Pregnyl) are good for up to 60 days in the refrigerator.",
            "Most fertility clinics have a general protocol for monitoring appointments that applies to IUI, embryo transfer, IVF, and egg freezing appointments. Here are a few things to know:\n\n" +
                    "• Timing: Monitoring appointments during ovarian stimulation are usually scheduled in the morning. Most clinics offer appointments starting around 7:00 AM and continue scheduling these appointments until about 1:00 PM.\n\n" +
                    "• What will happen at the appointment? At each appointment, you will have an ultrasound to monitor follicle growth and endometrial lining thickness as well as a blood draw to monitor hormone levels.\n\n" +
                    "• What happens next? After your appointment, your clinic will receive the blood test results and will update you about your progress, medication dosage, and instructions.",
            "There are two main reasons why clinics use birth control while preparing for a treatment cycle:\n\n" +
                    "• Suppresses ovulation: The suppression prevents the body from growing a dominant follicle and ovulating, keeping follicles immature and synchronized.\n\n" +
                    "• Flexibility in scheduling: It allows flexibility in start dates, helping to coordinate patient schedules.",
            "Ovarian Hyperstimulation Syndrome (OHSS) is an exaggerated response to ovarian stimulation medications. Symptoms include rapid bloating, shortness of breath, severe abdominal pain, nausea, and decreased urination. If you experience these symptoms, contact your clinic immediately.",
            "The generic form of Omnitrope is Somatropin, but it is not commonly prescribed for fertility purposes. There are other brand-name versions of growth hormone like Saizen and Zomacton that may be used during IVF.",
            "During IVF and egg freezing, when follicles reach between 16mm and 22mm, your doctor will give you a trigger shot. This final maturation prepares the eggs for fertilization during retrieval, which occurs about 36 hours after the shot.",
            "Both low dose hCG and Menopur are used during ovarian stimulation. Menopur is more common and includes LH and FSH, which stimulate follicle growth. Low dose hCG is sometimes used for advanced reproductive age or cases with poor embryo quality.",
            "The general rules are:\n" +
                    "* Under 35: See a specialist after 12 months of trying without success.\n" +
                    "* 35-39: See a specialist after 6 months of trying without success.\n" +
                    "* 40 and older: Seek help as soon as you start trying, due to age-related declines in fertility.",
            "Cycle day 1 is considered the first day of full flow bleeding. Spotting days before a full flow period should not be counted as cycle days.",
            "Spotting just before your full flow period starts is very normal. However, if you experience irregular spotting throughout your cycle, consult a doctor to determine the cause.",
            "Some questions to ask at your initial consultation include:\n\n" +
                    "* How many practitioners work in the office?\n" +
                    "* Will I see my doctor for all appointments or rotate between practitioners?\n" +
                    "* Who will be my main point of contact?\n" +
                    "* Do you offer supportive services like therapy or acupuncture?",
            "Platelet-Rich Plasma (PRP) Therapy involves centrifuging a person's blood to extract PRP, which can then be injected into the uterus or ovaries. It is being studied for its potential to treat infertility issues.",
            "Follicles in the ovaries house immature eggs. During IVF, ovarian stimulation medications cause multiple follicles to grow, resulting in mature eggs ready for retrieval.",
            "For subcutaneous injections, sitting is recommended for safety. For intramuscular injections, lying down may be easier. Choose a position you are comfortable with.",
            "The trigger shot prepares eggs for retrieval by initiating their final maturation. Common medications used are Lupron and hCG, administered 36 hours before retrieval."
        )

        // Set up ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, questions)
        binding.questionsList.adapter = adapter

        // Handle question click
        binding.questionsList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, AnswerActivity::class.java)
            intent.putExtra("question", questions[position])
            intent.putExtra("answer", answers[position])
            startActivity(intent)
        }
    }
}
