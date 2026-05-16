xspp ukan uvqq hbel
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
https://scam-pied.vercel.app/

# Experiment 3 – Creating and Analyzing Disk Images Using dc3dd and Autopsy

---

## 🎯 Aim
To create a forensic disk image and analyze digital evidence (files, deleted files, logs) using **dc3dd** and **Autopsy** in Kali Linux.

---

## 🧰 Tools Required
| Tool | Purpose |
|------|---------|
| Kali Linux | The forensic working environment |
| dc3dd | Creates forensic disk images with hash verification |
| Autopsy | GUI tool to investigate and analyze disk images |

---

## 💡 Quick Concept — What Are We Doing & Why?

Think of a **crime scene investigation**, but for computers.

When a crime happens digitally (data theft, fraud, tampering), investigators cannot just open the original storage device and poke around — that would **alter the evidence**. Instead, they:

1. Make an **exact bit-by-bit copy** of the disk → called a **Disk Image**
2. **Hash** both the original and the copy → if hashes match, the copy is 100% identical and untampered
3. Investigate only the **copy** using tools like Autopsy

**Key terms to remember:**

| Term | Simple Meaning |
|------|---------------|
| Disk Image | A perfect snapshot/copy of an entire storage device |
| Bit-by-bit copy | Every single bit (0 or 1) is copied — including deleted files |
| Hashing (MD5/SHA) | A fingerprint of the data — if even 1 bit changes, the hash changes |
| Autopsy | A digital "post-mortem" tool — examines what happened on a system |
| dc3dd | Like the `dd` command but built for forensics — adds hashing + logging |

---

## 📋 Procedure

---

### ✅ Step 1 – Open Terminal in Kali Linux

Press:
```
Ctrl + Alt + T
```
This opens the terminal window.

---

### ✅ Step 2 – Create Sample Evidence File

We create a text file that will act as "evidence" to be discovered later during analysis.

```bash
echo "Cybersecurity Lab Evidence" > evidence.txt
```

Verify the file was created:
```bash
ls
```

You should see `evidence.txt` listed in the output. ✅

> **Why this step?** In real forensics, the disk already has files on it. Here we manually create one so we have something to find during the Autopsy analysis later.

---

### ✅ Step 3 – Check Your Disk Partitions

Run this command to see all storage devices and their partitions:
```bash
lsblk
```

Example output:
```
NAME   MAJ:MIN RM  SIZE RO TYPE MOUNTPOINT
sda      8:0    0   20G  0 disk
├─sda1   8:1    0   19G  0 part /
└─sda2   8:2    0    1G  0 part [SWAP]
```

> We will use **`/dev/sda1`** as our source partition for the disk image. Note down your partition name.

---

### ✅ Step 4 – Create a Practice Disk to Image

Since we can't image the live system's OS partition directly (it's in use), we create a **fake practice disk file** to work with.

**Step 4a – Create a 100MB blank disk file:**
```bash
dd if=/dev/zero of=practice_disk.dd bs=1M count=100
```

| Parameter | Meaning |
|-----------|---------|
| `if=/dev/zero` | Input = a source of empty zero bytes |
| `of=practice_disk.dd` | Output = our new fake disk file |
| `bs=1M` | Block size = 1 Megabyte at a time |
| `count=100` | Copy 100 blocks = 100 MB total |

**Step 4b – Format it as a Linux file system (ext4):**
```bash
mkfs.ext4 practice_disk.dd
```

> **What this does:** Formats the blank file like a real disk so it can hold files and be read by Autopsy.

**Step 4c – Mount the disk and copy evidence into it:**
```bash
mkdir -p /mnt/practice
mount -o loop practice_disk.dd /mnt/practice
cp evidence.txt /mnt/practice/
umount /mnt/practice
```

| Command | Meaning |
|---------|---------|
| `mkdir -p /mnt/practice` | Create a folder to mount the disk to |
| `mount -o loop` | Mount the `.dd` file as if it were a real disk |
| `cp evidence.txt` | Copy our evidence file into the disk |
| `umount` | Safely unmount it after copying |

---

### ✅ Step 5 – Create Forensic Image Using dc3dd

Install dc3dd if not already installed:
```bash
sudo apt install dc3dd -y
```

Now create the forensic image with hash logging:
```bash
dc3dd if=practice_disk.dd of=/home/kali/disk_image.dd hash=md5 log=/home/kali/acquisition.log
```

| Parameter | Meaning |
|-----------|---------|
| `if=practice_disk.dd` | Input = our practice disk |
| `of=disk_image.dd` | Output = the forensic image file |
| `hash=md5` | Generate an MD5 hash to verify integrity |
| `log=acquisition.log` | Save all details to a log file |

---

### ✅ Step 6 – Verify the Forensic Image Was Created

Check the image file:
```bash
ls -lh /home/kali/disk_image.dd
```

Check the acquisition log:
```bash
cat /home/kali/acquisition.log
```

The log will contain:
- Total image size
- MD5 hash value of the original and the copy
- Timestamp of acquisition

> ✅ **If both hash values match → the image is an exact, untampered copy of the original.**

---

### ✅ Step 7 – Launch Autopsy

Start Autopsy from the terminal:
```bash
autopsy
```

The terminal will show:
```
Evidence Locker: /var/lib/autopsy
Start Autopsy by pointing your browser to:
http://localhost:9999/autopsy
```

Open your browser (Firefox in Kali) and go to:
```
http://localhost:9999/autopsy
```

---

### ✅ Step 8 – Create a New Case in Autopsy

In the Autopsy browser interface:

1. Click **"New Case"**
2. Fill in the case details:

| Field | Value to Enter |
|-------|---------------|
| Case Name | `CyberLab_Investigation` |
| Description | `Disk image forensic analysis` |
| Investigator | Your name |

3. Click **"New Case"** → then click **"Add Host"**

Fill in host details:

| Field | Value |
|-------|-------|
| Host Name | `KaliLabMachine` |
| Description | `Kali Linux VM used for forensic investigation` |
| Time Zone | Leave Blank |
| Time Skew | Leave Blank |
| Alert Hash DB | Leave Blank |
| Ignore Hash DB | Leave Blank |

4. Click **"Add Host"** → then click **"Add Image"**

---

### ✅ Step 9 – Import the Disk Image

1. Click **"Add Image File"**
2. Enter the full path to your image:
```
/home/kali/disk_image.dd
```
3. Select type: **Partition** (since we imaged a partition)
4. Click **"Next"** → then **"Add"**

Autopsy will begin processing the image.

---

### ✅ Step 10 – Analyze the Evidence

Once Autopsy loads the image, you can:

**View all files:**
- Click on the image name → **File Analysis**
- Browse folders and find `evidence.txt`

**Look for deleted files:**
- Files marked with a red ✖ are **deleted files** — Autopsy can still recover them since it's a bit-by-bit copy

**Check file details:**
- Click any file to see its content, metadata (created/modified time), and file path

**What to document in your observation:**
| Finding | What to Note |
|---------|-------------|
| Files found | Name, path, size |
| Deleted files | Any files marked as deleted |
| evidence.txt content | Should say "Cybersecurity Lab Evidence" |
| Hash values | From acquisition.log — both should match |

---

## 🔍 Observations Summary

| Observation | Details |
|------------|---------|
| Disk image created | `disk_image.dd` — 100 MB |
| Hash verified | MD5 hash of original and image match → integrity confirmed |
| Evidence file found | `evidence.txt` visible in Autopsy file browser |
| Deleted file recovery | Deleted files still visible due to bit-by-bit copy |
| Unencrypted data | File contents readable without decryption |

---

## 📌 Result

A forensic disk image was successfully created using **dc3dd** with MD5 hash verification. The image was imported into **Autopsy**, where files and forensic artifacts were identified and analyzed — demonstrating the complete digital forensics acquisition and investigation workflow.

---

## ⚠️ Precautions

| # | Precaution |
|---|-----------|
| 1 | Never investigate the **original** disk — always work on the image copy |
| 2 | Always **verify hash values** before and after imaging to confirm no tampering |
| 3 | Run `umount` before imaging to avoid capturing a disk in a dirty/inconsistent state |
| 4 | Ensure enough free disk space — image file size ≈ original disk size |

---

> 💬 **Exam/Viva Tips:**
> - **Why use dc3dd over regular dd?** → dc3dd adds built-in hashing and logging, which regular `dd` doesn't have — essential for court-admissible forensic evidence.
> - **Why does hashing matter?** → If the hash of the image matches the original, it proves the copy wasn't tampered with — this is called **chain of custody**.
> - **Why can deleted files be recovered?** → Deletion only removes the file's reference in the file system — the actual bits remain on disk until overwritten. A bit-by-bit copy captures those bits.
