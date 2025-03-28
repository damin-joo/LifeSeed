package com.example.LifeSeed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.LifeSeed.databinding.ActivityAnswerBinding

class AnswerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnswerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get Question Data from Intent
        val question = intent.getStringExtra("question")

        // Set Question Title
        binding.questionTitle.text = question

        // Set Corresponding Answer
        val answers = mapOf(
            "1. Can I mix my hCG (human chorionic gonadotropin) trigger shot in advance and how long can it be left out of the refrigerator once it's mixed?" to
                    "You can premix your hCG trigger shot. Once the powder is reconstituted using the provided liquid, it can safely be refrigerated for at least 30 days and certain brands (like Pregnyl) are good for up to 60 days in the refrigerator.",

            "2. What should I expect at my monitoring appointments?" to
                    "Most fertility clinics have a general protocol for monitoring appointments. Key points:\n\n" +
                    "• Timing: Morning appointments, typically starting around 7:00 AM.\n" +
                    "• At the appointment: Ultrasound to monitor follicle growth and blood tests.\n" +
                    "• After the appointment: Updated medication dosage and instructions based on results.",

            "3. Why is birth control sometimes used at the beginning of a cycle?" to
                    "Two main reasons:\n\n" +
                    "• Suppresses ovulation: Keeps follicles immature and synchronized for stimulation.\n" +
                    "• Flexibility in scheduling: Allows better coordination with personal schedules and clinic workflows.",

            "4. What is OHSS (Ovarian Hyperstimulation Syndrome) and what should I be looking for?" to
                    "OHSS is an exaggerated response to ovarian stimulation medications. Symptoms include bloating, shortness of breath, severe abdominal pain, nausea, and decreased urination. Contact your clinic if you experience these.",

            "5. Does Omnitrope have a generic alternative available?" to
                    "The generic form of Omnitrope is Somatropin. Other alternatives include Saizen and Zomacton. Your pharmacy may suggest these if Omnitrope is unavailable or costly.",

            "6. What happens to follicles after the trigger shot?" to
                    "The trigger shot finalizes egg maturation and releases eggs from follicle walls into follicular fluid. Retrieval occurs ~36 hours after the shot, during which eggs are drawn from the fluid.",

            "7. What is the difference between low dose hCG vs. Menopur? Which one is better?" to
                    "Both are used during ovarian stimulation. Menopur includes LH and FSH to stimulate follicle growth, while low dose hCG may help improve egg and embryo quality for certain patients.",

            "8. How long should I try to conceive before seeing a doctor? General Fertility" to
                    "• Under 35: Seek help after 12 months.\n" +
                    "• 35-39: Seek help after 6 months.\n" +
                    "• 40+: Seek help immediately.\n" +
                    "If you have irregular periods or a history of infertility, see a doctor sooner.",

            "9. How can I tell when menstrual cycle day 1 is?" to
                    "Cycle day 1 is the first day of 'full flow' bleeding. Spotting does not count as cycle days. Call your clinic when full flow begins to start treatment cycles.",

            "10. Is spotting before (not between) a period normal?" to
                    "Spotting before your period is normal and doesn't count as cycle days. Spotting throughout your cycle, however, may indicate issues like fibroids or endometriosis. Discuss with your doctor if this occurs.",

            "11. What are some questions to ask at my initial consultation that can improve my experience at my fertility clinic?" to
                    "Key questions:\n" +
                    "• How many practitioners work here?\n" +
                    "• Who will I see for my appointments?\n" +
                    "• Who will do my egg retrieval?\n" +
                    "• Who is my main contact?\n" +
                    "• What communication methods are used?\n" +
                    "• Are there after-hours support options?\n" +
                    "• Do you offer supportive services like therapy?",

            "12. What is PRP (Platelet-Rich Plasma Therapy) and when is it used?" to
                    "PRP uses platelets to aid in healing and tissue regeneration. It's being studied for improving endometrial lining, implantation failure, and ovarian rejuvenation. PRP is made from your own blood via centrifugation.",

            "13. What is the difference between a follicle and an egg?" to
                    "Follicles house immature eggs. During stimulation, multiple follicles grow, each containing an egg. Follicles measuring 16mm-22mm are most likely to contain mature eggs.",

            "14. Should I lie down when I inject myself?" to
                    "Most clinics recommend sitting for subcutaneous injections for safety. For intramuscular injections, lying down can help relax the muscle and allow easier administration.",

            "15. What is a trigger shot in IVF and egg freezing?" to
                    "A trigger shot finalizes egg maturation and prepares eggs for retrieval. It is typically administered ~36 hours before retrieval and may involve Lupron, hCG, or a combination."
        )

        binding.answerText.text = answers[question] ?: "Answer not available."

        // Set up back button
        binding.backButton.setOnClickListener {
            finish() // Close AnswerActivity and go back to FAQActivity
        }
    }
}
