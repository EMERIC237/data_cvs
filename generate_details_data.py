import pandas as pd
import faker
import json
import random

# Initialize Faker
fake = faker.Faker()

# Define the number of rows for each chunk
chunk_size = 10000
num_chunks = 7

# Define categories
categories = ['Fake News Articles', 'HR Policy', 'Support Documents', 'Knowledge Articles']

# Define LOBs
lob_dict = {
    "CIB": {
        "CorporateHierarchy": "CORPORATE & INVESTMENT BANK (S573050)",
        "CorporateHierarchy_Label": "Corporate & Investment Bank",
        "Hierarchy_ID": "S573050"
    },
    "CCB": {
        "CorporateHierarchy": "CONSUMER & COMMUNITY BANKING (S577006)",
        "CorporateHierarchy_Label": "Consumer & Community Banking",
        "Hierarchy_ID": "S577006"
    },
    "CB": {
        "CorporateHierarchy": "COMMERCIAL BANK (S570308)",
        "CorporateHierarchy_Label": "Commercial Bank",
        "Hierarchy_ID": "S570308"
    },
    "PB": {
        "CorporateHierarchy": "GLOBAL PRIVATE BANK (S578955)",
        "CorporateHierarchy_Label": "Private Bank",
        "Hierarchy_ID": "S578955"
    },
    "AWM": {
        "CorporateHierarchy": "ASSET & WEALTH MANAGEMENT (S572000)",
        "CorporateHierarchy_Label": "Asset & Wealth Management",
        "Hierarchy_ID": "S572000"
    },
    "Corporate": {
        "CorporateHierarchy": "CORPORATE SECTOR (S579899)",
        "CorporateHierarchy_Label": "Corporate Sector",
        "Hierarchy_ID": "S579899"
    },
    "JPMWM": {
        "CorporateHierarchy": "US WEALTH MANAGEMENT (S528916)",
        "CorporateHierarchy_Label": "US Wealth Management",
        "Hierarchy_ID": "S528916"
    }
}

# Define taxonomy
taxonomy_dict = {
    "audit": {
        "CorpTaxonomy": "Audit(AUDIT)",
        "CorpTaxonomy_Label": "Audit",
        "Taxonomy_ID": "AUDIT"
    },
    "risk": {
        "CorpTaxonomy": "Compliance(COMPL)",
        "CorpTaxonomy_Label": "Risk Management & Compliance",
        "Taxonomy_ID": "COMPL"
    },
    "risk_management": {
        "CorpTaxonomy": "Risk Management(RSKMGT)",
        "CorpTaxonomy_Label": "Risk Management",
        "Taxonomy_ID": "RSKMGT"
    },
    "tech": {
        "CorpTaxonomy": "Technology(TCHNOL)",
        "CorpTaxonomy_Label": "Technology",
        "Taxonomy_ID": "TCHNOL"
    },
    "hr": {
        "CorpTaxonomy": "Human Resources(HUMRSC)",
        "CorpTaxonomy_Label": "Human Resources",
        "Taxonomy_ID": "HUMRSC"
    },
    "finance": {
        "CorpTaxonomy": "Finance(FNANCE)",
        "CorpTaxonomy_Label": "Finance",
        "Taxonomy_ID": "FNANCE"
    },
    "other_cao": {
        "CorpTaxonomy": "Other CAO(OTHCAO)",
        "CorpTaxonomy_Label": "Other CAO",
        "Taxonomy_ID": "OTHCAO"
    },
    "control_management": {
        "CorpTaxonomy": "Control Management(CNTRLMGMT)",
        "CorpTaxonomy_Label": "Control Management",
        "Taxonomy_ID": "CNTRLMGMT"
    },
    "legal": {
        "CorpTaxonomy": "Legal(LEGAL)",
        "CorpTaxonomy_Label": "Legal",
        "Taxonomy_ID": "LEGAL"
    }
}

# Define countries and country codes
country_dict = {
    "AUS": "Australia",
    "BGD": "Bangladesh",
    "CHN": "China",
    "HKG": "Hong Kong",
    "IND": "India",
    "IDN": "Indonesia",
    "JPN": "Japan",
    "KOR": "Korea",
    "MYS": "Malaysia",
    "NZL": "New Zealand",
    "PHL": "Philippines",
    "SGP": "Singapore",
    "LKA": "Sri Lanka",
    "TWN": "Taiwan",
    "THA": "Thailand",
    "VNM": "Vietnam",
    "AUT": "Austria",
    "BHR": "Bahrain",
    "BEL": "Belgium",
    "BGR": "Bulgaria",
    "DNK": "Denmark",
    "EGY": "Egypt",
    "FIN": "Finland",
    "FRA": "France",
    "DEU": "Germany",
    "GRC": "Greece",
    "GGY": "Guernsey",
    "HUN": "Hungary",
    "IRL": "Ireland",
    "ISR": "Israel",
    "ITA": "Italy",
    "JEY": "Jersey"
}

# Function to generate a large amount of text
def generate_large_text(min_length=4000):
    text = ""
    while len(text) < min_length:
        text += fake.paragraph(nb_sentences=5)
    return text

# Function to choose a random dictionary item
def random_dict_item(dictionary):
    key = random.choice(list(dictionary.keys()))
    return key, dictionary[key]

# Function to convert dates to strings
def date_to_string(date):
    return date.strftime('%Y-%m-%d')

# Generate and save data in chunks
json_file_path = "fake_mixed_documents_70k.json"
csv_file_path = "fake_mixed_documents_70k.csv"

with open(json_file_path, 'w') as json_file:
    for chunk in range(num_chunks):
        # Generate fake data for the current chunk
        data_chunk = []
        csv_chunk = {
            "id": [],
            "title": [],
            "body": [],
            "tags": [],
            "updated_date": [],
            "created_date": [],
            "category": [],
            "author": [],
            "meta_data": [],
            "lob_CorporateHierarchy": [],
            "lob_CorporateHierarchy_Label": [],
            "lob_Hierarchy_ID": [],
            "CorpTaxonomy": [],
            "CorpTaxonomy_Label": [],
            "Taxonomy_ID": [],
            "country": [],
            "country_code": [],
            "categories": []
        }
        
        for i in range(chunk_size):
            lob_key, lob_value = random_dict_item(lob_dict)
            tax_key, tax_value = random_dict_item(taxonomy_dict)
            country_code, country_name = random_dict_item(country_dict)

            document = {
                "id": i + chunk * chunk_size + 1,
                "title": fake.sentence(nb_words=6),
                "body": generate_large_text(),
                "tags": [fake.word(ext_word_list=['urgent', 'important', 'update', 'review', 'policy'])],
                "updated_date": date_to_string(fake.date_this_decade()),
                "created_date": date_to_string(fake.date_this_decade()),
                "category": random.choice(categories),
                "author": fake.name(),
                "meta_data": fake.text(max_nb_chars=200),
                "lob_CorporateHierarchy": lob_value["CorporateHierarchy"],
                "lob_CorporateHierarchy_Label": lob_value["CorporateHierarchy_Label"],
                "lob_Hierarchy_ID": lob_value["Hierarchy_ID"],
                "CorpTaxonomy": tax_value["CorpTaxonomy"],
                "CorpTaxonomy_Label": tax_value["CorpTaxonomy_Label"],
                "Taxonomy_ID": tax_value["Taxonomy_ID"],
                "country": country_name,
                "country_code": country_code,
                "categories": [random.choice(categories)]
            }
            data_chunk.append(document)
            
            # Append data for CSV
            csv_chunk["id"].append(document["id"])
            csv_chunk["title"].append(document["title"])
            csv_chunk["body"].append(document["body"])
            csv_chunk["tags"].append(document["tags"])
            csv_chunk["updated_date"].append(document["updated_date"])
            csv_chunk["created_date"].append(document["created_date"])
            csv_chunk["category"].append(document["category"])
            csv_chunk["author"].append(document["author"])
            csv_chunk["meta_data"].append(document["meta_data"])
            csv_chunk["lob_CorporateHierarchy"].append(document["lob_CorporateHierarchy"])
            csv_chunk["lob_CorporateHierarchy_Label"].append(document["lob_CorporateHierarchy_Label"])
            csv_chunk["lob_Hierarchy_ID"].append(document["lob_Hierarchy_ID"])
            csv_chunk["CorpTaxonomy"].append(document["CorpTaxonomy"])
            csv_chunk["CorpTaxonomy_Label"].append(document["CorpTaxonomy_Label"])
            csv_chunk["Taxonomy_ID"].append(document["Taxonomy_ID"])
            csv_chunk["country"].append(document["country"])
            csv_chunk["country_code"].append(document["country_code"])
            csv_chunk["categories"].append(document["categories"])

        # Write chunk to JSON file
        json.dump(data_chunk, json_file)
        json_file.write('\n')
        
        # Create DataFrame for the current chunk and append to CSV file
        df_chunk = pd.DataFrame(csv_chunk)
        if chunk == 0:
            df_chunk.to_csv(csv_file_path, index=False)
        else:
            df_chunk.to_csv(csv_file_path, mode='a', header=False, index=False)

print(f"Data saved to {json_file_path} and {csv_file_path}")
