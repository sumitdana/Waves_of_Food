const functions = require("firebase-functions");
const nodemailer = require("nodemailer");

const gmailEmail = "miguel.07.diaz07oo@gmail.com";
const gmailPassword = "qghyhamzmdkqmjdo";

const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: gmailEmail,
        pass: gmailPassword,
    },
});

exports.sendOtpEmail = functions.https.onRequest((req, res) => {
    const email = req.body.email;
    const otp = Math.floor(100000 + Math.random() * 900000); // 6-digit OTP

    if (!email) {
        return res.status(400).send({ success: false, message: "Email is required" });
    }

    const mailOptions = {
        from: `Waves Of Food <${gmailEmail}>`,
        to: email,
        subject: "Your OTP Code",
        html: `<h2>Your OTP is: <strong>${otp}</strong></h2>`,
    };

    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            console.error("Error sending email:", error);
            return res.status(500).send({ success: false, message: "Failed to send OTP" });
        } else {
            console.log("OTP sent:", otp);
            return res.status(200).send({ success: true, otp: otp });
        }
    });
});