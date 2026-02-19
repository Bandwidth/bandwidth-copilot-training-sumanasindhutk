import csv
import re
import json
from pathlib import Path

def extract_order_ids_by_status(search_results_file, status):
    """
    Extract orderIds from search results where status matches the given status.
    
    Args:
        search_results_file: Path to the search results CSV file
        status: Status to filter by (e.g., 'FAILED', 'COMPLETE')
    
    Returns:
        Set of order IDs matching the given status
    """
    order_ids = set()
    status_filter = f'"status":"{status}"'
    
    try:
        with open(search_results_file, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                # Check if status matches
                if status_filter in row['_raw']:
                    # Extract orderId from the JSON in _raw column
                    match = re.search(r'"orderId":"([^"]+)"', row['_raw'])
                    if match:
                        order_ids.add(match.group(1))
    except FileNotFoundError:
        print(f"Error: File not found - {search_results_file}")
        return None
    
    return order_ids

def extract_message_from_raw(raw_string):
    """
    Extract the message field from the JSON in _raw column.
    
    Args:
        raw_string: The raw JSON string
    
    Returns:
        Extracted message or empty string
    """
    try:
        match = re.search(r'"message":"([^"]*)"', raw_string)
        if match:
            return match.group(1)
    except Exception:
        pass
    return ""

def extract_phone_numbers_from_raw(raw_string):
    """
    Extract the phoneNumbers array from the JSON in _raw column.
    
    Args:
        raw_string: The raw JSON string
    
    Returns:
        Comma-separated phone numbers or empty string
    """
    try:
        # Find phoneNumbers array in the JSON
        match = re.search(r'"phoneNumbers":\s*\[(.*?)\]', raw_string)
        if match:
            phone_array = match.group(1)
            # Extract individual phone numbers
            phones = re.findall(r'"([^"]*\+\d+[^"]*)"', phone_array)
            if phones:
                return ', '.join(phones)
    except Exception:
        pass
    return ""

def find_matching_rows(report_file, order_ids, status):
    """
    Find rows in report where any order ID appears in the _raw column.
    
    Args:
        report_file: Path to the report CSV file
        order_ids: Set of order IDs to match
        status: Status type (used for matchType column)
    
    Returns:
        Tuple of (matching_rows, fieldnames)
    """
    matching_rows = []
    fieldnames = None
    
    try:
        with open(report_file, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            fieldnames = reader.fieldnames
            
            for row in reader:
                # Check if any order ID appears in the _raw column
                for order_id in order_ids:
                    if order_id in row['_raw']:
                        # Add orderId as a new column
                        row['orderId'] = order_id
                        # Extract message from _raw column
                        row['message'] = extract_message_from_raw(row['_raw'])
                        # Extract phone numbers from _raw column
                        row['phoneNumbers'] = extract_phone_numbers_from_raw(row['_raw'])
                        row['matchType'] = status
                        matching_rows.append(row)
                        break  # Only add row once, even if multiple IDs match
    except FileNotFoundError:
        print(f"Error: File not found - {report_file}")
        return None, None
    
    return matching_rows, fieldnames

def save_matching_rows(output_file, matching_rows, fieldnames):
    """
    Save matching rows to a new CSV file with orderId, message, phoneNumbers, and matchType columns.
    
    Args:
        output_file: Path to the output CSV file
        matching_rows: List of rows to save
        fieldnames: Original fieldnames from the CSV
    """
    try:
        # Add new columns if not already present
        if fieldnames:
            new_fieldnames = list(fieldnames) + ['orderId', 'message', 'phoneNumbers', 'matchType']
        
        with open(output_file, 'w', newline='', encoding='utf-8') as f:
            writer = csv.DictWriter(f, fieldnames=new_fieldnames)
            writer.writeheader()
            writer.writerows(matching_rows)
        print(f"✓ Successfully saved {len(matching_rows)} matching rows to: {output_file}")
    except Exception as e:
        print(f"Error saving file: {e}")

def main():
    """Main function to process CSV files and find orders by status."""
    # File paths
    search_results_file = '/Users/skrishnaiah/Downloads/search-results-2026-02-19T01_47_17.680-0800.csv'
    report_file = '/Users/skrishnaiah/Downloads/search-results-2026-02-19T01_47_17.680-0800.csv'  # TODO: Update with actual report file path
    
    # Define statuses to process
    statuses = [
        {'status': 'FAILED', 'output': '/Users/skrishnaiah/Downloads/failed_order_matches.csv'},
        {'status': 'COMPLETE', 'output': '/Users/skrishnaiah/Downloads/complete_order_matches.csv'}
    ]
    
    print("Processing orders by status...\n")
    print("=" * 70)
    
    for status_config in statuses:
        status = status_config['status']
        output_file = status_config['output']
        
        print(f"\nSearching for {status} orders...\n")
        
        # Step 1: Extract order IDs by status
        print(f"Step 1: Extracting {status} order IDs from search results...")
        order_ids = extract_order_ids_by_status(search_results_file, status)
        if order_ids is None:
            continue
        print(f"✓ Found {len(order_ids)} {status} order IDs\n")
        
        if len(order_ids) > 0:
            print(f"Sample {status} order IDs:")
            for order_id in list(order_ids)[:5]:
                print(f"  - {order_id}")
            if len(order_ids) > 5:
                print(f"  ... and {len(order_ids) - 5} more\n")
        else:
            print(f"⚠ No {status} orders found in search results.\n")
            continue
        
        # Step 2: Find matches
        print(f"Step 2: Finding matching rows in report...")
        matching_rows, fieldnames = find_matching_rows(report_file, order_ids, status)
        if matching_rows is None:
            continue
        print(f"✓ Found {len(matching_rows)} matching rows from {status} orders\n")
        
        # Step 3: Save results
        if len(matching_rows) > 0:
            print(f"Step 3: Saving {status} results...")
            save_matching_rows(output_file, matching_rows, fieldnames)
        else:
            print(f"⚠ No matching rows found for {status} orders.\n")
        
        print("=" * 70)
    
    print("\n✓ Process completed successfully!")

if __name__ == "__main__":
    main()